alter table users
    add column if not exists password varchar(120) not null default '';
