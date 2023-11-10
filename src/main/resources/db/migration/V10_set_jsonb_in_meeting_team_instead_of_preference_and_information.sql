ALTER TABLE "meeting_team"
    ADD COLUMN "preference" jsonb default '[]'::jsonb,
    ADD COLUMN "information" jsonb default '[]'::jsonb;

DROP TABLE IF EXISTS "information";
DROP TABLE IF EXISTS "preference";

-- used this scripts to migrate data from preference and information to jsonb
-- do
-- $$
-- declare r record;
-- begin
--     for r in select meeting_team.id from meeting_team
--     loop
-- update meeting_team set preference = (
--     select
--         to_json(array_agg(p))
--     from
--         meeting_team c
--             inner join
--         preference p on p.id = c.id
--     where
--             p.meeting_team_id = r.id
--     group by
--         p.meeting_team_id
-- ) where meeting_team.id = r.id;
-- end loop;
-- end
-- $$;
