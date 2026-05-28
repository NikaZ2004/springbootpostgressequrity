package com.example.springbootpostgressecurity.repository;

import java.math.BigDecimal;

public interface EmployeeSalaryWindowProjection {
    Integer getEmployeeId();

    String getEmployeeName();

    String getDepartmentName();

    BigDecimal getSalary();

    Long getDepartmentSalaryRank();

    Long getDepartmentSalaryRowNumber();

    Double getDepartmentAverageSalary();
}
