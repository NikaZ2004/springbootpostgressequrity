package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.models.Department;
import com.example.springbootpostgressecurity.models.Employee;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(name = "EmployeeResponse", description = "Employee data")
public class EmployeeResponse {
    private Integer id;
    private String fullName;
    private Integer departmentId;
    private String departmentName;
    private BigDecimal salary;

    public static EmployeeResponse from(Employee employee) {
        Department department = employee.getDepartment();

        return EmployeeResponse.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .departmentId(department == null ? null : department.getId())
                .departmentName(department == null ? null : department.getName())
                .salary(employee.getSalary())
                .build();
    }
}
