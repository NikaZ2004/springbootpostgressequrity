package com.example.springbootpostgressecurity.repository;

import java.math.BigDecimal;

public interface EmployeeDepartmentAverageProjection {
    Integer getEmployeeId();

    String getEmployeeName();

    String getDepartmentName();

    BigDecimal getSalary();

    Double getDepartmentAverageSalary();
}
