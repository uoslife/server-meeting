ALTER TABLE preference
    ADD CONSTRAINT unique_meeting_team UNIQUE (meeting_team_id);

ALTER TABLE meeting_team
    RENAME COLUMN message TO course;

ALTER TABLE user_information
    ADD CONSTRAINT unique_user UNIQUE (user_id);
