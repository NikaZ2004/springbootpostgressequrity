package com.example.springbootpostgressecurity.controllers;

import com.example.springbootpostgressecurity.payload.request.DepartmentRequest;
import com.example.springbootpostgressecurity.payload.response.DepartmentEmployeeResponse;
import com.example.springbootpostgressecurity.payload.response.DepartmentResponse;
import com.example.springbootpostgressecurity.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @Operation(summary = "Get all departments")
    public List<DepartmentResponse> findAll() {
        List<DepartmentResponse> departments = departmentService.findAll();
        log.info("departments findAll count {}", departments.size());
        return departments;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get department by id")
    public DepartmentResponse findById(@PathVariable Integer id) {
        DepartmentResponse department = departmentService.findById(id);
        log.info("department findById id {}", id);
        return department;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/with-employees")
    @Operation(summary = "Get departments with employees using left join")
    public List<DepartmentEmployeeResponse> findDepartmentsWithEmployeesLeftJoin() {
        List<DepartmentEmployeeResponse> departments = departmentService.findDepartmentsWithEmployeesLeftJoin();
        log.info("departments withEmployees count {}", departments.size());
        return departments;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create department")
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse department = departmentService.create(request);
        log.info("department create id {}", department.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(department);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Update department")
    public DepartmentResponse update(@PathVariable Integer id, @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse department = departmentService.update(id, request);
        log.info("department update id {}", id);
        return department;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Delete department")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        departmentService.delete(id);
        log.info("department delete id {}", id);
        return ResponseEntity.noContent().build();
    }
}
