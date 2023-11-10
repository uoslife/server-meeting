create table payment
(
    id              uuid primary key not null,
    "created_at"    TIMESTAMP        NOT NULL,
    "updated_at"    TIMESTAMP        NOT NULL,
    address         varchar(255),
    amount          integer,
    date            TIMESTAMP        NOT NULL,
    name            varchar(255),
    payed_user_id   uuid,
    status          varchar(255),
    meeting_team_id bigint
)

