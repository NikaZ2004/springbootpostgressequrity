package com.example.springbootpostgressecurity.controllers;

import com.example.springbootpostgressecurity.payload.request.EmployeeRequest;
import com.example.springbootpostgressecurity.payload.response.EmployeeDepartmentAverageResponse;
import com.example.springbootpostgressecurity.payload.response.EmployeeResponse;
import com.example.springbootpostgressecurity.payload.response.EmployeeSalaryWindowResponse;
import com.example.springbootpostgressecurity.services.EmployeeService;
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
@RequestMapping("/api/employees")
@Tag(name = "Employee", description = "CRUD operations for employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @Operation(summary = "Get all employees")
    public List<EmployeeResponse> findAll() {
        return employeeService.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get employee by id")
    public EmployeeResponse findById(@PathVariable Integer id) {
        return employeeService.findById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-department")
    @Operation(summary = "Get employees by department id")
    public List<EmployeeResponse> findByDepartmentId(@RequestParam Integer departmentId) {
        return employeeService.findByDepartmentId(departmentId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sql/window-salary-rank")
    @Operation(summary = "Get employee salary rank by department using native SQL window functions")
    public List<EmployeeSalaryWindowResponse> findSalaryWindowsWithNativeSql() {
        return employeeService.findSalaryWindowsWithNativeSql();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/jpql/window-salary-rank")
    @Operation(summary = "Get employee salary rank by department using JPQL/HQL window functions")
    public List<EmployeeSalaryWindowResponse> findSalaryWindowsWithJpql() {
        return employeeService.findSalaryWindowsWithJpql();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/jpa/subquery/salary-above-department-average")
    @Operation(summary = "Get employees with salary above department average using JPA subquery")
    public List<EmployeeDepartmentAverageResponse> findEmployeesWithSalaryAboveDepartmentAverage() {
        return employeeService.findEmployeesWithSalaryAboveDepartmentAverage();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create employee")
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Update employee")
    public EmployeeResponse update(@PathVariable Integer id, @Valid @RequestBody EmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Delete employee")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
