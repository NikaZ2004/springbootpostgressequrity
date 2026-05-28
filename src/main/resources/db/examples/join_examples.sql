drop view if exists full_join_view;
drop view if exists right_join_view;
drop view if exists left_join_view;
drop view if exists inner_join_view;

drop table if exists project_assignments;
drop table if exists projects;
drop table if exists employees;
drop table if exists departments;

create table departments (
    id integer primary key,
    name varchar(100) not null unique
);

create table employees (
    id integer primary key,
    full_name varchar(100) not null,
    department_id integer,
    salary numeric(10, 2) not null,
    constraint fk_employees_departments
        foreign key (department_id) references departments (id)
);

create table projects (
    id integer primary key,
    name varchar(100) not null,
    department_id integer,
    budget numeric(12, 2) not null,
    constraint fk_projects_departments
        foreign key (department_id) references departments (id)
);

create table project_assignments (
    id integer primary key,
    employee_id integer not null,
    project_id integer not null,
    role varchar(100) not null,
    weekly_hours integer not null,
    constraint fk_project_assignments_employees
        foreign key (employee_id) references employees (id),
    constraint fk_project_assignments_projects
        foreign key (project_id) references projects (id)
);

insert into departments (id, name)
values
    (1, 'Engineering'),
    (2, 'Sales'),
    (3, 'HR'),
    (4, 'Marketing'),
    (5, 'Support');

insert into employees (id, full_name, department_id, salary)
values
    (1, 'Anna Ivanova', 1, 3200.00),
    (2, 'Boris Petrov', 1, 2900.00),
    (3, 'Viktor Sidorov', 2, 2400.00),
    (4, 'Elena Smirnova', null, 2100.00),
    (5, 'Pavel Kozlov', 3, 2300.00);

insert into projects (id, name, department_id, budget)
values
    (1, 'API Gateway', 1, 50000.00),
    (2, 'CRM Rollout', 2, 35000.00),
    (3, 'Onboarding', 3, 12000.00),
    (4, 'Brand Campaign', 4, 20000.00),
    (5, 'Legacy Cleanup', null, 8000.00);

insert into project_assignments (id, employee_id, project_id, role, weekly_hours)
values
    (1, 1, 1, 'Backend Developer', 24),
    (2, 2, 1, 'DevOps Engineer', 16),
    (3, 3, 2, 'Sales Analyst', 20),
    (4, 5, 3, 'HR Coordinator', 12),
    (5, 1, 5, 'Technical Lead', 8);

create view inner_join_view as
select
    d.id as department_id,
    d.name as department_name,
    e.full_name as employee_name,
    p.name as project_name,
    p.budget
from departments d
inner join employees e on e.department_id = d.id
inner join projects p on p.department_id = d.id;

create view left_join_view as
select
    d.id as department_id,
    d.name as department_name,
    e.full_name as employee_name,
    p.name as project_name,
    p.budget
from departments d
left join employees e on e.department_id = d.id
left join projects p on p.department_id = d.id;

create view right_join_view as
select
    d.id as department_id,
    d.name as department_name,
    e.full_name as employee_name,
    p.name as project_name,
    p.budget
from departments d
right join employees e on e.department_id = d.id
left join projects p on p.department_id = d.id;

create view full_join_view as
select
    coalesce(d.id, e.department_id, p.department_id) as department_id,
    d.name as department_name,
    e.full_name as employee_name,
    p.name as project_name,
    p.budget
from departments d
full outer join employees e on e.department_id = d.id
full outer join projects p on p.department_id = coalesce(d.id, e.department_id);

-- INNER JOIN: only rows where department, employee, and project all match.
select
    d.id as department_id,
    d.name as department_name,
    e.full_name as employee_name,
    p.name as project_name,
    p.budget
from departments d
inner join employees e on e.department_id = d.id
inner join projects p on p.department_id = d.id
order by d.id, e.id, p.id;

-- LEFT JOIN: all departments, plus matching employees and projects when they exist.
select
    d.id as department_id,
    d.name as department_name,
    e.full_name as employee_name,
    p.name as project_name,
    p.budget
from departments d
left join employees e on e.department_id = d.id
left join projects p on p.department_id = d.id
order by d.id, e.id, p.id;

-- RIGHT JOIN: all employees, plus department/project data when the employee has a department.
select
    d.id as department_id,
    d.name as department_name,
    e.full_name as employee_name,
    p.name as project_name,
    p.budget
from departments d
right join employees e on e.department_id = d.id
left join projects p on p.department_id = d.id
order by e.id, p.id;

-- FULL OUTER JOIN: all departments, all employees, and all projects,
-- including rows without matching department_id.
select
    coalesce(d.id, e.department_id, p.department_id) as department_id,
    d.name as department_name,
    e.full_name as employee_name,
    p.name as project_name,
    p.budget
from departments d
full outer join employees e on e.department_id = d.id
full outer join projects p on p.department_id = coalesce(d.id, e.department_id)
order by department_id nulls last, employee_name nulls last, project_name nulls last;

-- JOIN: project assignments with employee and project details.
select
    pa.id as assignment_id,
    e.full_name as employee_name,
    p.name as project_name,
    pa.role,
    pa.weekly_hours
from project_assignments pa
inner join employees e on e.id = pa.employee_id
inner join projects p on p.id = pa.project_id
order by pa.id;

-- GROUP BY: total assigned hours and assignment count for each project.
select
    p.name as project_name,
    count(pa.id) as assignments_count,
    coalesce(sum(pa.weekly_hours), 0) as total_weekly_hours
from projects p
left join project_assignments pa on pa.project_id = p.id
group by p.id, p.name
order by p.id;

-- WINDOW FUNCTIONS: rank employees by salary inside each department.
select
    e.id as employee_id,
    e.full_name as employee_name,
    d.name as department_name,
    e.salary,
    rank() over (
        partition by e.department_id
        order by e.salary desc, e.id
    ) as department_salary_rank,
    row_number() over (
        partition by e.department_id
        order by e.salary desc, e.id
    ) as department_salary_row_number,
    avg(e.salary) over (partition by e.department_id) as department_average_salary
from employees e
left join departments d on d.id = e.department_id
order by e.department_id nulls last, e.salary desc, e.id;

-- SUBQUERY: employees whose salary is above the average salary of their department.
select
    e.id as employee_id,
    e.full_name as employee_name,
    d.name as department_name,
    e.salary,
    (
        select avg(e2.salary)
        from employees e2
        where e2.department_id = e.department_id
    ) as department_average_salary
from employees e
inner join departments d on d.id = e.department_id
where e.salary > (
    select avg(e2.salary)
    from employees e2
    where e2.department_id = e.department_id
)
order by d.id, e.salary desc, e.id;
