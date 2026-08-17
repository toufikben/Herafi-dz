-- Herafi DZ - Supabase schema
-- Apply this file in the Supabase SQL Editor.
-- The public client must never receive the service_role key.

create extension if not exists pgcrypto;

create table if not exists public.profiles (
    id uuid primary key references auth.users(id) on delete cascade,
    display_name text not null check (char_length(display_name) between 2 and 80),
    phone text check (phone is null or char_length(phone) <= 30),
    role text not null default 'customer' check (role in ('customer', 'craftsman', 'admin')),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create or replace function public.handle_new_auth_user()
returns trigger language plpgsql security definer set search_path = public as $$
begin
    insert into public.profiles (id, display_name, phone, role)
    values (
        new.id,
        left(coalesce(new.raw_user_meta_data ->> 'full_name', split_part(new.email, '@', 1)), 80),
        nullif(left(coalesce(new.raw_user_meta_data ->> 'phone', ''), 30), ''),
        case when lower(coalesce(new.raw_user_meta_data ->> 'user_type', 'CLIENT')) = 'craftsman'
             then 'craftsman' else 'customer' end
    )
    on conflict (id) do update set
        display_name = excluded.display_name,
        phone = excluded.phone,
        role = excluded.role,
        updated_at = now();
    return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
after insert on auth.users
for each row execute function public.handle_new_auth_user();

create table if not exists public.craftsmen (
    id uuid primary key default gen_random_uuid(),
    owner_id uuid references public.profiles(id) on delete set null,
    name text not null check (char_length(name) between 2 and 120),
    category_key text not null,
    wilaya_code text not null,
    commune text not null default '' check (char_length(commune) <= 100),
    phone text not null check (char_length(phone) between 8 and 30),
    whatsapp text check (whatsapp is null or char_length(whatsapp) <= 30),
    description text not null default '' check (char_length(description) <= 2000),
    daily_rate_dzd integer check (daily_rate_dzd is null or daily_rate_dzd between 0 and 100000000),
    years_experience integer not null default 0 check (years_experience between 0 and 80),
    skills_csv text not null default '' check (char_length(skills_csv) <= 500),
    is_verified boolean not null default false,
    status text not null default 'pending' check (status in ('pending', 'published', 'suspended')),
    rating_score numeric(3,1) not null default 0 check (rating_score between 0 and 10),
    rating_count integer not null default 0 check (rating_count >= 0),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists public.reviews (
    id uuid primary key default gen_random_uuid(),
    craftsman_id uuid not null references public.craftsmen(id) on delete cascade,
    reviewer_id uuid not null references public.profiles(id) on delete cascade,
    score_ten numeric(3,1) not null check (score_ten between 0 and 10),
    comment text not null default '' check (char_length(comment) <= 500),
    created_at timestamptz not null default now(),
    unique (craftsman_id, reviewer_id)
);

create table if not exists public.bookmarks (
    user_id uuid not null references public.profiles(id) on delete cascade,
    craftsman_id uuid not null references public.craftsmen(id) on delete cascade,
    created_at timestamptz not null default now(),
    primary key (user_id, craftsman_id)
);

create table if not exists public.service_requests (
    id uuid primary key default gen_random_uuid(),
    client_request_id uuid not null default gen_random_uuid(),
    customer_id uuid not null references public.profiles(id) on delete cascade,
    craftsman_id uuid references public.craftsmen(id) on delete set null,
    category_key text not null,
    wilaya_code text not null,
    commune text not null default '' check (char_length(commune) <= 100),
    description text not null check (char_length(description) between 10 and 2000),
    status text not null default 'open' check (status in ('open', 'quoted', 'accepted', 'in_progress', 'completed', 'cancelled')),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

alter table public.service_requests add column if not exists craftsman_id uuid references public.craftsmen(id) on delete set null;
alter table public.service_requests add column if not exists client_request_id uuid default gen_random_uuid();
update public.service_requests set client_request_id = id where client_request_id is null;
alter table public.service_requests alter column client_request_id set not null;

create index if not exists craftsmen_search_idx on public.craftsmen (wilaya_code, category_key, status);
create unique index if not exists craftsmen_owner_unique_idx
    on public.craftsmen (owner_id)
    where owner_id is not null;
create index if not exists reviews_craftsman_idx on public.reviews (craftsman_id, created_at desc);
create unique index if not exists requests_client_idempotency_idx
    on public.service_requests (customer_id, client_request_id);
create index if not exists requests_customer_idx on public.service_requests (customer_id, created_at desc);
create index if not exists requests_craftsman_idx on public.service_requests (craftsman_id, created_at desc);

alter table public.profiles enable row level security;
alter table public.craftsmen enable row level security;
alter table public.reviews enable row level security;
alter table public.bookmarks enable row level security;
alter table public.service_requests enable row level security;

drop policy if exists "published craftsmen are public" on public.craftsmen;
drop policy if exists "authenticated users can propose craftsmen" on public.craftsmen;
drop policy if exists "owners can manage own craftsmen" on public.craftsmen;
drop policy if exists "profiles are visible to their owner" on public.profiles;
drop policy if exists "users can create their profile" on public.profiles;
drop policy if exists "users can update their profile" on public.profiles;
drop policy if exists "reviews are public for published craftsmen" on public.reviews;
drop policy if exists "users can create their own review" on public.reviews;
drop policy if exists "users can delete their own review" on public.reviews;
drop policy if exists "users manage their bookmarks" on public.bookmarks;
drop policy if exists "customers view their requests" on public.service_requests;
drop policy if exists "customers create requests" on public.service_requests;
drop policy if exists "customers update their requests" on public.service_requests;
drop policy if exists "assigned craftsmen view requests" on public.service_requests;
drop policy if exists "assigned craftsmen update requests" on public.service_requests;

create policy "published craftsmen are public" on public.craftsmen
    for select using (status = 'published' or owner_id = auth.uid());
create policy "authenticated users can propose craftsmen" on public.craftsmen
    for insert to authenticated with check (owner_id = auth.uid());
create policy "owners can manage own craftsmen" on public.craftsmen
    for update to authenticated using (owner_id = auth.uid())
    with check (owner_id = auth.uid());

create policy "profiles are visible to their owner" on public.profiles
    for select to authenticated using (id = auth.uid());
create policy "users can create their profile" on public.profiles
    for insert to authenticated with check (id = auth.uid());
create policy "users can update their profile" on public.profiles
    for update to authenticated using (id = auth.uid()) with check (id = auth.uid());

create policy "reviews are public for published craftsmen" on public.reviews
    for select using (exists (select 1 from public.craftsmen c where c.id = craftsman_id and c.status = 'published'));
create policy "users can create their own review" on public.reviews
    for insert to authenticated with check (reviewer_id = auth.uid());
create policy "users can delete their own review" on public.reviews
    for delete to authenticated using (reviewer_id = auth.uid());

create policy "users manage their bookmarks" on public.bookmarks
    for all to authenticated using (user_id = auth.uid()) with check (user_id = auth.uid());

create policy "customers view their requests" on public.service_requests
    for select to authenticated using (customer_id = auth.uid());
create policy "customers create requests" on public.service_requests
    for insert to authenticated with check (
        customer_id = auth.uid()
        and status = 'open'
        and (craftsman_id is null or exists (
            select 1 from public.craftsmen c
            where c.id = craftsman_id and c.status = 'published'
        ))
    );
create policy "customers update their requests" on public.service_requests
    for update to authenticated using (customer_id = auth.uid()) with check (customer_id = auth.uid());
create policy "assigned craftsmen view requests" on public.service_requests
    for select to authenticated using (
        exists (select 1 from public.craftsmen c where c.id = craftsman_id and c.owner_id = auth.uid())
    );
create policy "assigned craftsmen update requests" on public.service_requests
    for update to authenticated using (
        exists (select 1 from public.craftsmen c where c.id = craftsman_id and c.owner_id = auth.uid())
    ) with check (
        exists (select 1 from public.craftsmen c where c.id = craftsman_id and c.owner_id = auth.uid())
    );

create or replace function public.refresh_craftsman_rating(cid uuid)
returns void language plpgsql security definer set search_path = public as $$
begin
    update public.craftsmen set
        rating_score = coalesce((select round(avg(score_ten)::numeric, 1) from public.reviews where craftsman_id = cid), 0),
        rating_count = (select count(*)::int from public.reviews where craftsman_id = cid)
    where id = cid;
end;
$$;

create or replace function public.on_review_change()
returns trigger language plpgsql security definer set search_path = public as $$
begin
    if tg_op = 'delete' then
        perform public.refresh_craftsman_rating(old.craftsman_id);
    else
        perform public.refresh_craftsman_rating(new.craftsman_id);
    end if;
    return new;
end;
$$;

drop trigger if exists reviews_rating_refresh on public.reviews;
create trigger reviews_rating_refresh
    after insert or update or delete on public.reviews
    for each row execute function public.on_review_change();

create or replace function public.set_updated_at()
returns trigger language plpgsql as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

drop trigger if exists profiles_updated_at on public.profiles;
create trigger profiles_updated_at before update on public.profiles for each row execute function public.set_updated_at();
drop trigger if exists craftsmen_updated_at on public.craftsmen;
create trigger craftsmen_updated_at before update on public.craftsmen for each row execute function public.set_updated_at();
drop trigger if exists requests_updated_at on public.service_requests;
create trigger requests_updated_at before update on public.service_requests for each row execute function public.set_updated_at();

-- ============================================================================
-- security hardening (v1.2.0): protect service_requests critical fields
-- ============================================================================

-- trigger: only the assigned craftsman may change status / craftsman_id;
-- customer_id can never be changed after creation.
create or replace function public.service_requests_immutable_guard()
returns trigger
language plpgsql
security definer
as $$
begin
    -- customer_id is immutable
    if new.customer_id is distinct from old.customer_id then
        raise exception 'customer_id cannot be changed';
    end if;

    -- only the assigned craftsman may touch status / craftsman_id
    if (select exists (
            select 1 from public.craftsmen c
            where c.id = old.craftsman_id and c.owner_id = auth.uid()
       )) then
        if new.craftsman_id is distinct from old.craftsman_id then
            raise exception 'assigned craftsman may not change craftsman_id';
        end if;
    else
        if new.status is distinct from old.status then
            raise exception 'status may only be changed by the assigned craftsman';
        end if;
        if new.craftsman_id is distinct from old.craftsman_id then
            raise exception 'craftsman_id may only be changed by assignment at creation';
        end if;
    end if;

    return new;
end;
$$;

drop trigger if exists service_requests_immutable_guard on public.service_requests;
create trigger service_requests_immutable_guard
    before update on public.service_requests
    for each row execute function public.service_requests_immutable_guard();

-- revoke write access from anon role entirely (RLS policies are
-- authenticated-only anyway; this removes any accidental anon write path)
revoke update, insert, delete on public.service_requests from anon;
