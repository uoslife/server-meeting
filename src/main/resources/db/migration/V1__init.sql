create table match (
    id bigserial not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    date timestamp(6),
    female_team_id bigint,
    male_team_id bigint,
    primary key (id)
)

create table meeting_team (
    id bigserial not null,
    code varchar(255) not null,
    compatibility jsonb,
    information jsonb,
    message varchar(255),
    name varchar(255),
    preference jsonb,
    season integer,
    type varchar(255) not null,
    leader_id bigint,
    primary key (id)
)

create table payment (
    id uuid not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    imp_uid varchar(255),
    marchant_uid varchar(255),
    paid_date timestamp(6),
    pay_method varchar(255),
    pg varchar(255),
    price integer,
    status varchar(255),
    user_id bigint,
    primary key (id)
)

create table "user" (
    id bigint not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    kakao_talk_id varchar(255),
    name varchar(255),
    phone_number varchar(255),
    user_personal_information jsonb,
    team_id bigint,
    primary key (id)
)

alter table if exists meeting_team
    add constraint UK_91houb7fbd63y3ev5fpldjl0y unique (leader_id)
