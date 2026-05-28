package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.models.Employee;
import com.example.springbootpostgressecurity.models.Project;
import com.example.springbootpostgressecurity.models.ProjectAssignment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "ProjectAssignmentResponse", description = "Project assignment data")
public class ProjectAssignmentResponse {
    private Integer id;
    private Integer employeeId;
    private String employeeName;
    private Integer projectId;
    private String projectName;
    private String role;
    private Integer weeklyHours;

    public static ProjectAssignmentResponse from(ProjectAssignment assignment) {
        Employee employee = assignment.getEmployee();
        Project project = assignment.getProject();

        return ProjectAssignmentResponse.builder()
                .id(assignment.getId())
                .employeeId(employee.getId())
                .employeeName(employee.getFullName())
                .projectId(project.getId())
                .projectName(project.getName())
                .role(assignment.getRole())
                .weeklyHours(assignment.getWeeklyHours())
                .build();
    }
}
