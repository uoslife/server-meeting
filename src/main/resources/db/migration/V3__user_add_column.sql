CREATE TYPE "StudentType" AS ENUM (
    'UNDERGRADUATE',
    'POSTGRADUATE',
    'GRADUATE'
    );

ALTER Table "user"
    ADD COLUMN "kakao_talk_id"  VARCHAR(255),
    ADD COLUMN "student_type"   "StudentType" Default 'UNDERGRADUATE',
    ADD COLUMN "student_number" VARCHAR(255),
    ADD Column "smoking"        BOOLEAN,
    ADD COLUMN "spirit_animal"  VARCHAR(255),
    ADD COLUMN "mbti"           VARCHAR(255),
    ADD COLUMN "interest"       VARCHAR(255)


