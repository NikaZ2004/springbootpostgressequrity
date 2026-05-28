alter table users
    add column if not exists name varchar(20);

update users
set name = username
where name is null or name = '';

alter table users
    alter column name set not null;
