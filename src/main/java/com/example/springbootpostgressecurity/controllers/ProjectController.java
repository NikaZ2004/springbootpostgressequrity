package com.example.springbootpostgressecurity.controllers;

import com.example.springbootpostgressecurity.payload.request.ProjectRequest;
import com.example.springbootpostgressecurity.payload.response.ProjectResponse;
import com.example.springbootpostgressecurity.services.ProjectService;
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
@RequestMapping("/api/projects")
@Tag(name = "Project", description = "CRUD operations for projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @Operation(summary = "Get all projects")
    public List<ProjectResponse> findAll() {
        return projectService.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get project by id")
    public ProjectResponse findById(@PathVariable Integer id) {
        return projectService.findById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-department")
    @Operation(summary = "Get projects by department id")
    public List<ProjectResponse> findByDepartmentId(@RequestParam Integer departmentId) {
        return projectService.findByDepartmentId(departmentId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create project")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Update project")
    public ProjectResponse update(@PathVariable Integer id, @Valid @RequestBody ProjectRequest request) {
        return projectService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Delete project")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
