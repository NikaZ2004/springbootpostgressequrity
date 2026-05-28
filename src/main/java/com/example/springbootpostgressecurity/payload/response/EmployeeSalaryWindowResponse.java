package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.repository.EmployeeSalaryWindowProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(name = "EmployeeSalaryWindowResponse", description = "Employee salary analytics from window functions")
public class EmployeeSalaryWindowResponse {
    private Integer employeeId;
    private String employeeName;
    private String departmentName;
    private BigDecimal salary;
    private Long departmentSalaryRank;
    private Long departmentSalaryRowNumber;
    private Double departmentAverageSalary;

    public static EmployeeSalaryWindowResponse from(EmployeeSalaryWindowProjection projection) {
        return EmployeeSalaryWindowResponse.builder()
                .employeeId(projection.getEmployeeId())
                .employeeName(projection.getEmployeeName())
                .departmentName(projection.getDepartmentName())
                .salary(projection.getSalary())
                .departmentSalaryRank(projection.getDepartmentSalaryRank())
                .departmentSalaryRowNumber(projection.getDepartmentSalaryRowNumber())
                .departmentAverageSalary(projection.getDepartmentAverageSalary())
                .build();
    }
}
