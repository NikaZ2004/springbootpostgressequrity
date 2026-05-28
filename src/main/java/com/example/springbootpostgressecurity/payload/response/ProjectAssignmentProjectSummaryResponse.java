package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.repository.ProjectAssignmentProjectSummaryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "ProjectAssignmentProjectSummaryResponse", description = "Project assignment summary from JPQL group by")
public class ProjectAssignmentProjectSummaryResponse {
    private String projectName;
    private Long assignmentsCount;
    private Long totalWeeklyHours;

    public static ProjectAssignmentProjectSummaryResponse from(ProjectAssignmentProjectSummaryProjection projection) {
        Long totalWeeklyHours = projection.getTotalWeeklyHours();

        return ProjectAssignmentProjectSummaryResponse.builder()
                .projectName(projection.getProjectName())
                .assignmentsCount(projection.getAssignmentsCount())
                .totalWeeklyHours(totalWeeklyHours == null ? 0L : totalWeeklyHours)
                .build();
    }
}
