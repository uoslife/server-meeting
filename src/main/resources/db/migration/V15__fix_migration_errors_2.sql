ALTER TABLE user_team
DROP
CONSTRAINT user_team_team_id_fkey;

ALTER TABLE user_team
DROP
CONSTRAINT user_team_user_id_fkey;

ALTER TABLE "user"
    ADD email VARCHAR(255);

ALTER TABLE "user"
    ADD kakao_talk_id VARCHAR(255);

ALTER TABLE "user"
    ADD team_id BIGINT;

ALTER TABLE payment
    ADD imp_uid VARCHAR(255);

ALTER TABLE payment
    ADD marchant_uid VARCHAR(255);

ALTER TABLE payment
    ADD paid_date TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE payment
    ADD pay_method VARCHAR(255);

ALTER TABLE payment
    ADD user_id UUID;

ALTER TABLE payment
    ADD pg VARCHAR(255);

ALTER TABLE payment
    ADD price INTEGER;

ALTER TABLE meeting_team
    ADD leader_id UUID;

ALTER TABLE meeting_team
    ADD type VARCHAR(255);

ALTER TABLE meeting_team
    ALTER COLUMN type SET NOT NULL;

ALTER TABLE match
    ADD CONSTRAINT uc_match_female_team UNIQUE (female_team_id);

ALTER TABLE match
    ADD CONSTRAINT uc_match_male_team UNIQUE (male_team_id);

ALTER TABLE meeting_team
    ADD CONSTRAINT uc_meeting_team_leader UNIQUE (leader_id);

ALTER TABLE payment
    ADD CONSTRAINT uc_payment_payment UNIQUE (payment_id);

ALTER TABLE "user"
    ADD CONSTRAINT uc_user_email UNIQUE (email);

ALTER TABLE payment
    ADD CONSTRAINT payment_id FOREIGN KEY (id) REFERENCES "user";

ALTER TABLE meeting_team
    ADD CONSTRAINT FK_MEETING_TEAM_ON_LEADER FOREIGN KEY (leader_id) REFERENCES "user" (id);

ALTER TABLE "user"
    ADD CONSTRAINT FK_USER_ON_TEAM FOREIGN KEY (team_id) REFERENCES meeting_team (id);

DROP TABLE user_team CASCADE;

ALTER TABLE payment
DROP
COLUMN address;

ALTER TABLE payment
DROP
COLUMN amount;

ALTER TABLE payment
DROP
COLUMN date;

ALTER TABLE payment
DROP
COLUMN meeting_team_id;

ALTER TABLE payment
DROP
COLUMN name;

ALTER TABLE payment
DROP
COLUMN payed_user_id;

ALTER TABLE "user"
DROP
COLUMN nickname;

ALTER TABLE "user"
DROP
COLUMN profile_picture;

ALTER TABLE match
    ALTER COLUMN female_team_id DROP NOT NULL;

ALTER TABLE match
    ALTER COLUMN male_team_id DROP NOT NULL;

ALTER TABLE meeting_team
    ALTER COLUMN season DROP NOT NULL;
