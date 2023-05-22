ALTER TABLE "information"
    DROP COLUMN "meeting_time";

ALTER TABLE "information"
    ADD COLUMN "meeting_time" VARCHAR(255);
