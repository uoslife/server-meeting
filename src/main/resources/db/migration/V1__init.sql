-- Drop tables if they exist
DROP TABLE IF EXISTS match;
DROP TABLE IF EXISTS meeting_team;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS "user";

-- Create meeting_user table
CREATE TABLE meeting_user (
                              id BIGINT NOT NULL PRIMARY KEY,
                              created_at TIMESTAMP(6) NOT NULL,
                              updated_at TIMESTAMP(6) NOT NULL,
                              kakao_talk_id VARCHAR(255) UNIQUE,
                              name VARCHAR(255),
                              phone_number VARCHAR(255),
                              user_personal_information JSONB,
                              team_id BIGINT
);

-- Create match table
CREATE TABLE match (
                       id BIGSERIAL PRIMARY KEY,
                       created_at TIMESTAMP(6) NOT NULL,
                       updated_at TIMESTAMP(6) NOT NULL,
                       date TIMESTAMP(6),
                       female_team_id BIGINT,
                       male_team_id BIGINT
);

-- Create meeting_team table
CREATE TABLE meeting_team (
                              id BIGSERIAL PRIMARY KEY,
                              code VARCHAR(255) NOT NULL,
                              compatibility JSONB,
                              information JSONB,
                              message VARCHAR(255),
                              name VARCHAR(255),
                              preference JSONB,
                              season INTEGER,
                              type VARCHAR(255) NOT NULL,
                              leader_id BIGINT UNIQUE
);

-- Create payment table
CREATE TABLE payment (
                         id UUID PRIMARY KEY,
                         created_at TIMESTAMP(6) NOT NULL,
                         updated_at TIMESTAMP(6) NOT NULL,
                         imp_uid VARCHAR(255),
                         marchant_uid VARCHAR(255),
                         paid_date TIMESTAMP(6),
                         pay_method VARCHAR(255),
                         pg VARCHAR(255),
                         price INTEGER,
                         status VARCHAR(255),
                         user_id BIGINT,
                         FOREIGN KEY (user_id) REFERENCES meeting_user (id)
);
