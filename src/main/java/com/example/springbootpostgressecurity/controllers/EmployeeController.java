package com.example.springbootpostgressecurity.controllers;

import com.example.springbootpostgressecurity.payload.request.EmployeeRequest;
import com.example.springbootpostgressecurity.payload.response.EmployeeDepartmentAverageResponse;
import com.example.springbootpostgressecurity.payload.response.EmployeeResponse;
import com.example.springbootpostgressecurity.payload.response.EmployeeSalaryWindowResponse;
import com.example.springbootpostgressecurity.services.EmployeeService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee", description = "CRUD operations for employees")
public class EmployeeController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @Operation(summary = "Get all employees")
    public List<EmployeeResponse> findAll() {
        List<EmployeeResponse> employees = employeeService.findAll();
        log.info("employees findAll count {}", employees.size());
        return employees;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get employee by id")
    public EmployeeResponse findById(@PathVariable Integer id) {
        EmployeeResponse employee = employeeService.findById(id);
        log.info("employee findById id {}", id);
        return employee;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-department")
    @Operation(summary = "Get employees by department id")
    public List<EmployeeResponse> findByDepartmentId(@RequestParam Integer departmentId) {
        List<EmployeeResponse> employees = employeeService.findByDepartmentId(departmentId);
        log.info("employees findByDepartmentId departmentId {} count {}", departmentId, employees.size());
        return employees;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sql/window-salary-rank")
    @Operation(summary = "Get employee salary rank by department using native SQL window functions")
    public List<EmployeeSalaryWindowResponse> findSalaryWindowsWithNativeSql() {
        List<EmployeeSalaryWindowResponse> employees = employeeService.findSalaryWindowsWithNativeSql();
        log.info("employees salaryWindows nativeSql count {}", employees.size());
        return employees;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/jpql/window-salary-rank")
    @Operation(summary = "Get employee salary rank by department using JPQL/HQL window functions")
    public List<EmployeeSalaryWindowResponse> findSalaryWindowsWithJpql() {
        List<EmployeeSalaryWindowResponse> employees = employeeService.findSalaryWindowsWithJpql();
        log.info("employees salaryWindows jpql count {}", employees.size());
        return employees;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/jpa/subquery/salary-above-department-average")
    @Operation(summary = "Get employees with salary above department average using JPA subquery")
    public List<EmployeeDepartmentAverageResponse> findEmployeesWithSalaryAboveDepartmentAverage() {
        List<EmployeeDepartmentAverageResponse> employees = employeeService.findEmployeesWithSalaryAboveDepartmentAverage();
        log.info("employees salaryAboveDepartmentAverage count {}", employees.size());
        return employees;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create employee")
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.create(request);
        log.info("employee create id {}", employee.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Update employee")
    public EmployeeResponse update(@PathVariable Integer id, @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.update(id, request);
        log.info("employee update id {}", id);
        return employee;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Delete employee")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        employeeService.delete(id);
        log.info("employee delete id {}", id);
        return ResponseEntity.noContent().build();
    }
}
