package com.example.springbootpostgressecurity.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "project_assignments", indexes = {
        @Index(name = "idx_project_assignments_employee_project_id", columnList = "employee_id, project_id, id"),
        @Index(name = "idx_project_assignments_project_employee_id", columnList = "project_id, employee_id, id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAssignment {
    @Id
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_project_assignments_employees")
    )
    private Employee employee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_project_assignments_projects")
    )
    private Project project;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String role;

    @NotNull
    @Min(1)
    @Max(80)
    @Column(name = "weekly_hours", nullable = false)
    private Integer weeklyHours;
}
