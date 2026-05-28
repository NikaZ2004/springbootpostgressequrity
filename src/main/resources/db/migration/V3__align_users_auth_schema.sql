alter table users
    add column if not exists username varchar(20);

alter table users
    add column if not exists password varchar(120);

alter table users
    add column if not exists user_game_id bigint;

update users
set username = 'user' || id
where username is null or username = '';

with duplicate_usernames as (
    select ctid, id, row_number() over (partition by username order by id) as row_number
    from users
)
update users u
set username = 'user' || u.id
from duplicate_usernames d
where u.ctid = d.ctid
  and d.row_number > 1;

update users u
set password = coalesce(
    (
        select gu.password_hash
        from game_users gu
        where gu.email = u.email
        limit 1
    ),
    ''
)
where u.password is null;

alter table users
    alter column username set not null;

alter table users
    alter column password set not null;

create unique index if not exists ux_users_username on users (username);
create unique index if not exists ux_users_email on users (email);
create unique index if not exists ux_users_user_game_id on users (user_game_id);

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_users_game_users'
          and conrelid = 'users'::regclass
    ) then
        alter table users
            add constraint fk_users_game_users
            foreign key (user_game_id) references game_users (id);
    end if;
end $$;
