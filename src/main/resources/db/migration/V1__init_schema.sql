create table game_users (
    id bigserial primary key,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    role varchar(20) not null,
    status varchar(20) not null,
    email_verified boolean not null,
    last_login_at timestamp(6) with time zone,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null
);

create index idx_game_users_email on game_users (email);

create table roles (
    id serial primary key,
    name varchar(20)
);

create table users (
    id bigserial primary key,
    username varchar(20) not null unique,
    email varchar(50) not null unique,
    password varchar(120) not null,
    user_game_id bigint unique,
    constraint fk_users_game_users
        foreign key (user_game_id) references game_users (id)
);

create table user_roles (
    user_id bigint not null,
    role_id integer not null,
    primary key (user_id, role_id),
    constraint fk_user_roles_users
        foreign key (user_id) references users (id),
    constraint fk_user_roles_roles
        foreign key (role_id) references roles (id)
);

create table profiles (
    id bigserial primary key,
    user_id bigint not null unique,
    summoner_name varchar(100),
    region varchar(20),
    main_role varchar(20),
    rank varchar(20),
    bio varchar(500),
    age integer,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    constraint fk_profiles_game_users
        foreign key (user_id) references game_users (id)
);

create table profile_game_roles (
    profile_id bigint not null,
    game_role varchar(20) not null,
    constraint fk_profile_game_roles_profiles
        foreign key (profile_id) references profiles (id)
);

insert into roles (name)
values ('ROLE_USER'), ('ROLE_MODERATOR'), ('ROLE_ADMIN');
