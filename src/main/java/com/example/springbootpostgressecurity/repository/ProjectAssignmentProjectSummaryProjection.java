package com.example.springbootpostgressecurity.repository;

public interface ProjectAssignmentProjectSummaryProjection {
    String getProjectName();

    Long getAssignmentsCount();

    Long getTotalWeeklyHours();
}
