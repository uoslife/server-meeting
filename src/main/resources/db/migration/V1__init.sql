CREATE TYPE "GenderType" AS ENUM (
    'MALE',
    'FEMALE'
    );

CREATE TYPE "TeamType" AS ENUM (
    'SINGLE',
    'TRIPLE'
    );

CREATE TYPE "DepartmentNameType" AS ENUM (
    'ADMINISTRATION', 'INTERNATIONAL_RELATIONS', 'ECONOMICS', 'SOCIAL_WELFARE', 'TAXATION',
    'BUSINESS', 'CHEMICAL_ENGINEERING', 'MECHATRONICS', 'MATERIALS_ENGINEERING', 'ELECTRICAL_ENGINEERING',
    'CIVIL_ENGINEERING', 'COMPUTER_SCIENCE', 'ENVIRONMENTAL_HORTICULTURE', 'MATHEMATICS', 'PHYSICS',
    'LIFE_SCIENCES', 'STATISTICS', 'ENGLISH_LITERATURE', 'KOREAN_LITERATURE', 'HISTORY', 'PHILOSOPHY',
    'CHINESE_CULTURE', 'URBAN_ADMINISTRATION', 'URBAN_SOCIOLOGY', 'GEOINFORMATION_ENGINEERING', 'ARCHITECTURE_ARCHITECTURAL',
    'ARCHITECTURE_ENGINEERING', 'URBAN_ENGINEERING', 'TRANSPORTATION_ENGINEERING', 'LANDSCAPE_ARCHITECTURE',
    'ENVIRONMENTAL_ENGINEERING', 'MUSIC', 'INDUSTRIAL_DESIGN_VISUAL', 'INDUSTRIAL_DESIGN_INDUSTRIAL',
    'ENVIRONMENTAL_SCULPTURE', 'SPORTS_SCIENCE', 'LIBERAL_ARTS', 'APPLIED_CHEMISTRY', 'ARTIFICIAL_INTELLIGENCE'
    );


CREATE TABLE "user"
(
    "id"              UUID PRIMARY KEY,
    "birth_year"      INTEGER,
    "gender"          "GenderType",
    "phone_number"    VARCHAR(255),
    "profile_picture" VARCHAR(255),
    "nickname"        VARCHAR(255),
    "name"            VARCHAR(255),
    "department"      "DepartmentNameType",
    "created_at"      TIMESTAMP NOT NULL,
    "updated_at"      TIMESTAMP NOT NULL
);

CREATE TABLE "user_team"
(
    "team_id"   bigint     NOT NULL,
    "user_id"   UUID       NOT NULL,
    "is_leader" BOOLEAN    NOT NULL,
    "type"      "TeamType" NOT NULL,
    PRIMARY KEY ("team_id", "user_id")
);

CREATE TABLE "report"
(
    "id"             bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "user_id"        UUID         NOT NULL,
    "category"       VARCHAR(255) NOT NULL,
    "text"           VARCHAR(255) NOT NULL,
    "admin_response" VARCHAR(255),
    "created_at"     TIMESTAMP    NOT NULL,
    "updated_at"     TIMESTAMP    NOT NULL
);

CREATE TABLE "meeting_team"
(
    "id"     bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "season" INTEGER      NOT NULL,
    "code"   VARCHAR(255) NOT NULL
);

CREATE TABLE "preference"
(
    "id"                      bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "team_id"                 bigint       NOT NULL,
    "age_range_preference"    VARCHAR(255) NOT NULL,
    "height_range_preference" VARCHAR(255) NOT NULL,
    "filter_condition"        VARCHAR(255) NOT NULL,
    "distance_condition"      VARCHAR(255) NOT NULL
);

CREATE TABLE "information"
(
    "id"               bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "team_id"          bigint       NOT NULL,
    "meeting_location" VARCHAR(255) NOT NULL,
    "meeting_time"     TIMESTAMP    NOT NULL,
    "filter_info"      VARCHAR(255) NOT NULL,
    "distance_info"    VARCHAR(255) NOT NULL,
    "about_me"         VARCHAR(255)
);

CREATE TABLE "match"
(
    "id"             bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "date"           TIMESTAMP,
    "male_team_id"   bigint    NOT NULL,
    "female_team_id" bigint    NOT NULL,
    "created_at"     TIMESTAMP NOT NULL,
    "updated_at"     TIMESTAMP NOT NULL
);

CREATE TABLE "compatibility"
(
    "id"             bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "score"          INTEGER,
    "male_team_id"   bigint    NOT NULL,
    "female_team_id" bigint    NOT NULL,
    "created_at"     TIMESTAMP NOT NULL,
    "updated_at"     TIMESTAMP NOT NULL
);

CREATE TABLE "compatibility_priority"
(
    "id"     bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "name"   VARCHAR(255) NOT NULL,
    "weight" INTEGER      NOT NULL
);

ALTER TABLE "user_team"
    ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "user_team"
    ADD FOREIGN KEY ("team_id") REFERENCES "meeting_team" ("id");

ALTER TABLE "report"
    ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "preference"
    ADD FOREIGN KEY ("team_id") REFERENCES "meeting_team" ("id");

ALTER TABLE "information"
    ADD FOREIGN KEY ("team_id") REFERENCES "meeting_team" ("id");

ALTER TABLE "match"
    ADD FOREIGN KEY ("male_team_id") REFERENCES "meeting_team" ("id");

ALTER TABLE "match"
    ADD FOREIGN KEY ("female_team_id") REFERENCES "meeting_team" ("id");

ALTER TABLE "compatibility"
    ADD FOREIGN KEY ("male_team_id") REFERENCES "meeting_team" ("id");

ALTER TABLE "compatibility"
    ADD FOREIGN KEY ("female_team_id") REFERENCES "meeting_team" ("id");
