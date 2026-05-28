package com.example.springbootpostgressecurity.repository;

import com.example.springbootpostgressecurity.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByDepartment_Id(Integer departmentId);

    boolean existsByDepartment_Id(Integer departmentId);

    @Query(value = """
            select
                e.id as "employeeId",
                e.full_name as "employeeName",
                d.name as "departmentName",
                e.salary as "salary",
                rank() over (
                    partition by e.department_id
                    order by e.salary desc, e.id
                ) as "departmentSalaryRank",
                row_number() over (
                    partition by e.department_id
                    order by e.salary desc, e.id
                ) as "departmentSalaryRowNumber",
                cast(avg(e.salary) over (partition by e.department_id) as double precision)
                    as "departmentAverageSalary"
            from employees e
            left join departments d on d.id = e.department_id
            order by e.department_id nulls last, e.salary desc, e.id
            """, nativeQuery = true)
    List<EmployeeSalaryWindowProjection> findSalaryWindowsWithNativeSql();

    @Query("""
            select
                e.id as employeeId,
                e.fullName as employeeName,
                d.name as departmentName,
                e.salary as salary,
                rank() over (
                    partition by d.id
                    order by e.salary desc, e.id
                ) as departmentSalaryRank,
                row_number() over (
                    partition by d.id
                    order by e.salary desc, e.id
                ) as departmentSalaryRowNumber,
                avg(e.salary) over (partition by d.id) as departmentAverageSalary
            from Employee e
            left join e.department d
            order by d.id, e.salary desc, e.id
            """)
    List<EmployeeSalaryWindowProjection> findSalaryWindowsWithJpql();

    @Query("""
            select
                e.id as employeeId,
                e.fullName as employeeName,
                d.name as departmentName,
                e.salary as salary,
                (
                    select avg(employeeInDepartment.salary)
                    from Employee employeeInDepartment
                    where employeeInDepartment.department = e.department
                ) as departmentAverageSalary
            from Employee e
            join e.department d
            where e.salary > (
                select avg(employeeInDepartment.salary)
                from Employee employeeInDepartment
                where employeeInDepartment.department = e.department
            )
            order by d.id, e.salary desc, e.id
            """)
    List<EmployeeDepartmentAverageProjection> findEmployeesWithSalaryAboveDepartmentAverage();
}
