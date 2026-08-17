-- Herafi DZ - Real-time notifications
-- Apply in Supabase SQL Editor. Enables instant push between client and craftsman.

create table if not exists public.notifications (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles(id) on delete cascade,
    kind text not null check (kind in (
        'request_new',        -- new service request assigned to your craftsman profile
        'request_status',     -- a request you sent changed status
        'request_cancelled',  -- a request you sent was cancelled
        'request_accepted',   -- your request was accepted by the craftsman
        'price_quoted',       -- craftsman quoted a price on your request
        'review_received'     -- you received a new review (craftsman)
    )),
    ref_type text not null check (ref_type in ('service_request', 'craftsman', 'review', 'profile')),
    ref_id uuid not null,
    title text not null check (char_length(title) between 1 and 150),
    body text not null default '' check (char_length(body) <= 500),
    is_read boolean not null default false,
    created_at timestamptz not null default now()
);

create index if not exists notifications_user_idx on public.notifications (user_id, created_at desc);
create index if not exists notifications_unread_idx on public.notifications (user_id, is_read, created_at desc);

alter table public.notifications enable row level security;

drop policy if exists "users read their own notifications" on public.notifications;
drop policy if exists "users delete their own notifications" on public.notifications;
-- Internal inserts happen via security definer functions, so client INSERT is blocked on purpose.

create policy "users read their own notifications" on public.notifications
    for select to authenticated using (user_id = auth.uid());
create policy "users delete their own notifications" on public.notifications
    for delete to authenticated using (user_id = auth.uid());

-- Mark-as-read updates only their own rows
drop policy if exists "users update their own notifications" on public.notifications;
create policy "users update their own notifications" on public.notifications
    for update to authenticated using (user_id = auth.uid())
    with check (user_id = auth.uid());

-- Helper: broadcast + store a notification (security definer so triggers can call it)
create or replace function public.push_notification(
    target_user uuid,
    p_kind text,
    p_ref_type text,
    p_ref_id uuid,
    p_title text,
    p_body text
) returns void language plpgsql security definer set search_path = public as $$
begin
    insert into public.notifications (user_id, kind, ref_type, ref_id, title, body)
    values (target_user, p_kind, p_ref_type, p_ref_id, p_title, p_body);
    perform pg_notify('herafi_notifications', json_build_object(
        'user_id', target_user,
        'kind', p_kind,
        'ref_type', p_ref_type,
        'ref_id', p_ref_id,
        'title', p_title,
        'body', p_body
    )::text);
end;
$$;

-- Notify the craftsman when a new request is created
create or replace function public.on_request_created()
returns trigger language plpgsql security definer set search_path = public as $$
declare
    c_owner uuid;
    c_name text;
begin
    if new.craftsman_id is not null and new.status = 'open' then
        select c.owner_id, c.name into c_owner, c_name
        from public.craftsmen c where c.id = new.craftsman_id;
        if c_owner is not null then
            perform public.push_notification(
                c_owner, 'request_new', 'service_request', new.id,
                'طلب خدمة جديد',
                coalesce(left(new.description, 80), '')
            );
        end if;
    end if;
    return new;
end;
$$;

-- Notify the customer when the craftsman changes the request status
create or replace function public.on_request_status_changed()
returns trigger language plpgsql security definer set search_path = public as $$
declare
    c_name text;
begin
    if new.status is distinct from old.status and old.craftsman_id is not null then
        select c.name into c_name from public.craftsmen c where c.id = old.craftsman_id;
        if new.status = 'cancelled' then
            perform public.push_notification(
                new.customer_id, 'request_cancelled', 'service_request', new.id,
                'تم إلغاء الطلب',
                coalesce(c_name, 'الحرفي') || ' ألغى الطلب'
            );
        elsif new.status = 'quoted' then
            perform public.push_notification(
                new.customer_id, 'price_quoted', 'service_request', new.id,
                'عرض سعر جديد',
                coalesce(c_name, 'الحرفي') || ' أرسل عرض سعر'
            );
        elsif new.status = 'accepted' then
            perform public.push_notification(
                new.customer_id, 'request_accepted', 'service_request', new.id,
                'تم قبول الطلب',
                coalesce(c_name, 'الحرفي') || ' قبل طلبك وبدأ العمل'
            );
        elsif new.status = 'in_progress' then
            perform public.push_notification(
                new.customer_id, 'request_status', 'service_request', new.id,
                'الطلب قيد التنفيذ',
                coalesce(c_name, 'الحرفي') || ' يعمل الآن على طلبك'
            );
        elsif new.status = 'completed' then
            perform public.push_notification(
                new.customer_id, 'request_status', 'service_request', new.id,
                'اكتمل الطلب',
                coalesce(c_name, 'الحرفي') || ' أنهى العمل على طلبك'
            );
        end if;
    end if;
    return new;
end;
$$;

drop trigger if exists requests_notify_created on public.service_requests;
create trigger requests_notify_created
    after insert on public.service_requests
    for each row execute function public.on_request_created();

drop trigger if exists requests_notify_status on public.service_requests;
create trigger requests_notify_status
    after update on public.service_requests
    for each row execute function public.on_request_status_changed();

-- Listen for Realtime on this table (must be set AFTER RLS + triggers so replication is enabled)
alter publication supabase_realtime add table public.notifications;
