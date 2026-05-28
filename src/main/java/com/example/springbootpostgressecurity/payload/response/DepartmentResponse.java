package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.models.Department;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "DepartmentResponse", description = "Department data")
public class DepartmentResponse {
    private Integer id;
    private String name;

    public static DepartmentResponse from(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }
}
