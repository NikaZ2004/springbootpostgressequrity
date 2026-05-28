create or replace function get_user_role_overview(p_min_role_count integer default 0)
returns table (
    id bigint,
    username varchar,
    name varchar,
    email varchar,
    email_domain text,
    role_count bigint,
    roles text
)
language sql
stable
as $$
    select
        u.id,
        u.username,
        u.name,
        u.email,
        split_part(u.email, '@', 2) as email_domain,
        count(r.id) as role_count,
        coalesce(string_agg(r.name, ', ' order by r.name), '') as roles
    from users u
    left join user_roles ur on ur.user_id = u.id
    left join roles r on r.id = ur.role_id
    group by u.id, u.username, u.name, u.email
    having count(r.id) >= greatest(coalesce(p_min_role_count, 0), 0)
    order by role_count desc, u.username asc;
$$;

create or replace procedure grant_role_to_user(
    p_user_id bigint,
    p_role_name varchar
)
language plpgsql
as $$
declare
    v_role_id integer;
begin
    if not exists (select 1 from users where id = p_user_id) then
        raise exception 'User with id % not found', p_user_id using errcode = 'P0002';
    end if;

    select r.id
    into v_role_id
    from roles r
    where r.name = p_role_name;

    if v_role_id is null then
        raise exception 'Role % not found', p_role_name using errcode = 'P0002';
    end if;

    insert into user_roles (user_id, role_id)
    values (p_user_id, v_role_id)
    on conflict do nothing;
end;
$$;
