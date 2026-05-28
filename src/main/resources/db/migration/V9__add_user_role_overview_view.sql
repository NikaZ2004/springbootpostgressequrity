create or replace view user_role_overview as
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
group by u.id, u.username, u.name, u.email;
