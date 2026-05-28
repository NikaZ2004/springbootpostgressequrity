alter table users
    add column if not exists username varchar(20);

update users
set username = left(regexp_replace(coalesce(email, 'user' || id), '[^a-zA-Z0-9_]', '_', 'g'), 20)
where username is null or username = '';

with duplicate_usernames as (
    select ctid, id, row_number() over (partition by username order by id) as row_number
    from users
)
update users u
set username = left('user' || u.id, 20)
from duplicate_usernames d
where u.ctid = d.ctid
  and d.row_number > 1;

alter table users
    alter column username set not null;

create unique index if not exists ux_users_username on users (username);
