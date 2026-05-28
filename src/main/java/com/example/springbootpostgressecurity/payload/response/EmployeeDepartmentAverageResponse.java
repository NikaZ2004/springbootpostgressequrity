package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.repository.EmployeeDepartmentAverageProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(name = "EmployeeDepartmentAverageResponse", description = "Employee salary compared with department average")
public class EmployeeDepartmentAverageResponse {
    private Integer employeeId;
    private String employeeName;
    private String departmentName;
    private BigDecimal salary;
    private Double departmentAverageSalary;

    public static EmployeeDepartmentAverageResponse from(EmployeeDepartmentAverageProjection projection) {
        return EmployeeDepartmentAverageResponse.builder()
                .employeeId(projection.getEmployeeId())
                .employeeName(projection.getEmployeeName())
                .departmentName(projection.getDepartmentName())
                .salary(projection.getSalary())
                .departmentAverageSalary(projection.getDepartmentAverageSalary())
                .build();
    }
}
