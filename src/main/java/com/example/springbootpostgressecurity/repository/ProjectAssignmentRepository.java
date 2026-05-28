package com.example.springbootpostgressecurity.repository;

import com.example.springbootpostgressecurity.models.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Integer> {
    List<ProjectAssignment> findByEmployee_Id(Integer employeeId);

    List<ProjectAssignment> findByProject_Id(Integer projectId);

    boolean existsByEmployee_Id(Integer employeeId);

    boolean existsByProject_Id(Integer projectId);

    @Query("""
            select
                p.name as projectName,
                e.fullName as employeeName,
                pa.role as role,
                pa.weeklyHours as weeklyHours
            from ProjectAssignment pa
            join pa.project p
            join pa.employee e
            order by p.id, e.id, pa.id
            """)
    List<ProjectAssignmentDetailsProjection> findAssignmentDetailsJoin();

    @Query("""
            select
                p.name as projectName,
                count(pa.id) as assignmentsCount,
                sum(pa.weeklyHours) as totalWeeklyHours
            from Project p
            left join p.assignments pa
            group by p.id, p.name
            order by p.id
            """)
    List<ProjectAssignmentProjectSummaryProjection> groupAssignmentsByProject();
}
