do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'chk_employees_salary_non_negative'
          and conrelid = 'employees'::regclass
    ) then
        alter table employees
            add constraint chk_employees_salary_non_negative
            check (salary >= 0) not valid;
    end if;
end $$;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'chk_projects_budget_non_negative'
          and conrelid = 'projects'::regclass
    ) then
        alter table projects
            add constraint chk_projects_budget_non_negative
            check (budget >= 0) not valid;
    end if;
end $$;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'chk_project_assignments_weekly_hours_range'
          and conrelid = 'project_assignments'::regclass
    ) then
        alter table project_assignments
            add constraint chk_project_assignments_weekly_hours_range
            check (weekly_hours between 1 and 80) not valid;
    end if;
end $$;

create or replace view department_workload_overview as
with employee_stats as (
    select
        department_id,
        count(*) as employees_count,
        avg(salary) as average_salary
    from employees
    group by department_id
),
project_stats as (
    select
        department_id,
        count(*) as projects_count,
        sum(budget) as total_budget
    from projects
    group by department_id
),
assignment_stats as (
    select
        p.department_id,
        count(pa.id) as assignments_count,
        sum(pa.weekly_hours) as total_weekly_hours
    from projects p
    left join project_assignments pa on pa.project_id = p.id
    group by p.department_id
)
select
    d.id as department_id,
    d.name as department_name,
    coalesce(es.employees_count, 0) as employees_count,
    coalesce(ps.projects_count, 0) as projects_count,
    coalesce(asg.assignments_count, 0) as assignments_count,
    coalesce(asg.total_weekly_hours, 0) as total_weekly_hours,
    coalesce(ps.total_budget, 0) as total_budget,
    es.average_salary
from departments d
left join employee_stats es on es.department_id = d.id
left join project_stats ps on ps.department_id = d.id
left join assignment_stats asg on asg.department_id = d.id;

create index if not exists idx_employees_department_salary_desc
    on employees (department_id, salary desc, id);

create index if not exists idx_projects_department_budget_desc
    on projects (department_id, budget desc, id);

create index if not exists idx_project_assignments_project_weekly_hours
    on project_assignments (project_id, weekly_hours, id);
