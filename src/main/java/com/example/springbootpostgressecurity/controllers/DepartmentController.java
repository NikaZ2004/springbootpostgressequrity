package com.example.springbootpostgressecurity.controllers;

import com.example.springbootpostgressecurity.payload.request.DepartmentRequest;
import com.example.springbootpostgressecurity.payload.response.DepartmentEmployeeResponse;
import com.example.springbootpostgressecurity.payload.response.DepartmentResponse;
import com.example.springbootpostgressecurity.services.DepartmentService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/departments")
@Tag(name = "Department", description = "CRUD operations for departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @Operation(summary = "Get all departments")
    public List<DepartmentResponse> findAll() {
        return departmentService.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get department by id")
    public DepartmentResponse findById(@PathVariable Integer id) {
        return departmentService.findById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/with-employees")
    @Operation(summary = "Get departments with employees using left join")
    public List<DepartmentEmployeeResponse> findDepartmentsWithEmployeesLeftJoin() {
        return departmentService.findDepartmentsWithEmployeesLeftJoin();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create department")
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Update department")
    public DepartmentResponse update(@PathVariable Integer id, @Valid @RequestBody DepartmentRequest request) {
        return departmentService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Delete department")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
