create extension if not exists pg_trgm;

create table if not exists departments (
    id integer primary key,
    name varchar(100) not null unique
);

create table if not exists employees (
    id integer primary key,
    full_name varchar(100) not null,
    department_id integer,
    salary numeric(10, 2) not null,
    constraint fk_employees_departments
        foreign key (department_id) references departments (id)
);

create table if not exists projects (
    id integer primary key,
    name varchar(100) not null,
    department_id integer,
    budget numeric(12, 2) not null,
    constraint fk_projects_departments
        foreign key (department_id) references departments (id)
);

create table if not exists project_assignments (
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

create index if not exists idx_users_username_trgm
    on users using gin (lower(username) gin_trgm_ops);

create index if not exists idx_users_email_trgm
    on users using gin (lower(email) gin_trgm_ops);

create index if not exists idx_users_email_domain
    on users ((split_part(email, '@', 2)));

create index if not exists idx_roles_name
    on roles (name);

create index if not exists idx_user_roles_role_id_user_id
    on user_roles (role_id, user_id);

create index if not exists idx_game_users_email_trgm
    on game_users using gin (lower(email) gin_trgm_ops);

create index if not exists idx_game_users_status_email_verified
    on game_users (status, email_verified);

create index if not exists idx_game_users_role
    on game_users (role);

create index if not exists idx_game_users_created_at_desc
    on game_users (created_at desc);

create index if not exists idx_profile_game_roles_profile_id
    on profile_game_roles (profile_id);

create index if not exists idx_profile_game_roles_game_role_profile_id
    on profile_game_roles (game_role, profile_id);

create index if not exists idx_employees_department_id_id
    on employees (department_id, id);

create index if not exists idx_projects_department_id_id
    on projects (department_id, id);

create index if not exists idx_project_assignments_employee_project_id
    on project_assignments (employee_id, project_id, id);

create index if not exists idx_project_assignments_project_employee_id
    on project_assignments (project_id, employee_id, id);
