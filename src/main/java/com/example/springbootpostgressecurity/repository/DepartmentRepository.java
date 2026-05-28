package com.example.springbootpostgressecurity.repository;

import com.example.springbootpostgressecurity.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByName(String name);

    boolean existsByName(String name);

    @Query("""
            select
                d.name as departmentName,
                e.fullName as employeeName
            from Department d
            left join d.employees e
            """)
    List<DepartmentEmployeeProjection> findDepartmentsWithEmployeesLeftJoin();

    @Query("""
            select
                d.name as department,
                count(p.id) as projectsCount
            from Department d
            left join d.projects p
            group by d.id, d.name
            order by d.id
            """)
    List<DepartmentProjectsCountProjection> countProjectsByDepartment();
}
