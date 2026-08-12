from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
repo = (ROOT / "app/src/main/java/com/example/data/ServiceRequestRepository.kt").read_text()
api = (ROOT / "app/src/main/java/com/example/data/remote/SupabaseApi.kt").read_text()
schema = (ROOT / "supabase/schema.sql").read_text()
dao = (ROOT / "app/src/main/java/com/example/data/db/ServiceRequestDao.kt").read_text()

required_remote_fields = {
    "id", "customer_id", "craftsman_id", "category_key", "wilaya_code",
    "commune", "description", "status", "created_at", "updated_at"
}
remote_block = re.search(r"data class RemoteServiceRequest\((.*?)\n\)", api, re.S)
assert remote_block, "RemoteServiceRequest is missing"
remote_fields = set(re.findall(r"val\s+(\w+)\s*:", remote_block.group(1)))
assert required_remote_fields <= remote_fields, (required_remote_fields - remote_fields)

for field in required_remote_fields - {"created_at", "updated_at"}:
    assert re.search(rf"\b{field}\b", schema), f"schema field missing: {field}"

assert "@POST(\"rest/v1/service_requests\")" in api
assert "Prefer: return=representation" in api
assert "@GET(\"rest/v1/service_requests\")" in api
assert "customer_id = auth.uid()" in schema
assert "customer_id uuid not null references public.profiles(id)" in schema
assert "create trigger on_auth_user_created" in schema
assert "handle_new_auth_user" in schema
assert "SYNC_FAILED" in repo and "getPendingForCustomer" in repo
assert "dao.markSynced" in repo and "dao.markSyncState" in repo
assert "WHERE customerId = :customerId" in dao

# Ensure local-to-remote IDs are normalized only for remotely imported craftsmen.
assert 'craftsmanId = craftsmanId?.removePrefix("remote_")' in repo
assert 'toRemoteCraftsmanId' in repo
assert 'craftsman_id = localRequest.craftsmanId.toRemoteCraftsmanId()' in repo
assert 'id = existing?.id ?: "remote_$id"' in repo

print("PASS: service request REST/local contract checks")
print("PASS: pending/failed retry and synced transitions are wired")
print("PASS: customer ownership policy and local query are present")
print("PASS: remote request field mapping is complete")
