package com.example.springbootpostgressecurity.controllers;

import com.example.springbootpostgressecurity.payload.request.ProjectAssignmentRequest;
import com.example.springbootpostgressecurity.payload.response.ProjectAssignmentDetailsResponse;
import com.example.springbootpostgressecurity.payload.response.ProjectAssignmentProjectSummaryResponse;
import com.example.springbootpostgressecurity.payload.response.ProjectAssignmentResponse;
import com.example.springbootpostgressecurity.services.ProjectAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/project-assignments")
@Tag(name = "Project Assignment", description = "CRUD operations and JPQL examples for project assignments")
public class ProjectAssignmentController {
    private final ProjectAssignmentService projectAssignmentService;

    public ProjectAssignmentController(ProjectAssignmentService projectAssignmentService) {
        this.projectAssignmentService = projectAssignmentService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @Operation(summary = "Get all project assignments")
    public List<ProjectAssignmentResponse> findAll() {
        return projectAssignmentService.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get project assignment by id")
    public ProjectAssignmentResponse findById(@PathVariable Integer id) {
        return projectAssignmentService.findById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-employee")
    @Operation(summary = "Get project assignments by employee id")
    public List<ProjectAssignmentResponse> findByEmployeeId(@RequestParam Integer employeeId) {
        return projectAssignmentService.findByEmployeeId(employeeId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-project")
    @Operation(summary = "Get project assignments by project id")
    public List<ProjectAssignmentResponse> findByProjectId(@RequestParam Integer projectId) {
        return projectAssignmentService.findByProjectId(projectId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/jpql/join-details")
    @Operation(summary = "Get assignment details using JPQL join")
    public List<ProjectAssignmentDetailsResponse> findAssignmentDetailsJoin() {
        return projectAssignmentService.findAssignmentDetailsJoin();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/jpql/group-by-project")
    @Operation(summary = "Get assignment counts and hours grouped by project using JPQL group by")
    public List<ProjectAssignmentProjectSummaryResponse> groupAssignmentsByProject() {
        return projectAssignmentService.groupAssignmentsByProject();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create project assignment")
    public ResponseEntity<ProjectAssignmentResponse> create(@Valid @RequestBody ProjectAssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectAssignmentService.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Update project assignment")
    public ProjectAssignmentResponse update(
            @PathVariable Integer id,
            @Valid @RequestBody ProjectAssignmentRequest request) {
        return projectAssignmentService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Delete project assignment")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        projectAssignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
