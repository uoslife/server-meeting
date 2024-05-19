ALTER TABLE "user"
ALTER COLUMN id TYPE BIGINT USING id::bigint;

ALTER TABLE "meeting_team"
ALTER COLUMN leader_id TYPE BIGINT USING leader_id::bigint;

ALTER TABLE "payment"
ALTER COLUMN user_id TYPE BIGINT USING user_id::bigint;
