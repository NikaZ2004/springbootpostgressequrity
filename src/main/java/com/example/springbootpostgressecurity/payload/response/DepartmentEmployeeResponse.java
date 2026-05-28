package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.repository.DepartmentEmployeeProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "DepartmentEmployeeResponse", description = "Department with employee data")
public class DepartmentEmployeeResponse {
    private String departmentName;
    private String employeeName;

    public static DepartmentEmployeeResponse from(DepartmentEmployeeProjection projection) {
        return DepartmentEmployeeResponse.builder()
                .departmentName(projection.getDepartmentName())
                .employeeName(projection.getEmployeeName())
                .build();
    }
}
