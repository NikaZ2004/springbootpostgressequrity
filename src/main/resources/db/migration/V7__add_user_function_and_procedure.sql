create or replace function find_user_by_username(p_username varchar)
returns setof users
language sql
stable
as $$
    select *
    from users
    where username = p_username;
$$;

create or replace procedure update_user_name_by_id(
    p_user_id bigint,
    p_name varchar
)
language plpgsql
as $$
begin
    update users
    set name = p_name
    where id = p_user_id;
end;
$$;
