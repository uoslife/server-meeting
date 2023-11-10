DROP TABLE IF EXISTS "compatibility";
ALTER TABLE "meeting_team"
    ADD COLUMN "compatibility" JSONB NULL;
