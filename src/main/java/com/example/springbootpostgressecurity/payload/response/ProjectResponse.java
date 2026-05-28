package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.models.Department;
import com.example.springbootpostgressecurity.models.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(name = "ProjectResponse", description = "Project data")
public class ProjectResponse {
    private Integer id;
    private String name;
    private Integer departmentId;
    private String departmentName;
    private BigDecimal budget;

    public static ProjectResponse from(Project project) {
        Department department = project.getDepartment();

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .departmentId(department == null ? null : department.getId())
                .departmentName(department == null ? null : department.getName())
                .budget(project.getBudget())
                .build();
    }
}
