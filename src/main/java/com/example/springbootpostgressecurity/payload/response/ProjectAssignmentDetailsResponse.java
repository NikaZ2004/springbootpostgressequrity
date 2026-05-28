package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.repository.ProjectAssignmentDetailsProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "ProjectAssignmentDetailsResponse", description = "Project assignment details from JPQL join")
public class ProjectAssignmentDetailsResponse {
    private String projectName;
    private String employeeName;
    private String role;
    private Integer weeklyHours;

    public static ProjectAssignmentDetailsResponse from(ProjectAssignmentDetailsProjection projection) {
        return ProjectAssignmentDetailsResponse.builder()
                .projectName(projection.getProjectName())
                .employeeName(projection.getEmployeeName())
                .role(projection.getRole())
                .weeklyHours(projection.getWeeklyHours())
                .build();
    }
}
