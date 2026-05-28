package com.example.springbootpostgressecurity.services;

import com.example.springbootpostgressecurity.models.Department;
import com.example.springbootpostgressecurity.models.Employee;
import com.example.springbootpostgressecurity.payload.request.EmployeeRequest;
import com.example.springbootpostgressecurity.payload.response.EmployeeDepartmentAverageResponse;
import com.example.springbootpostgressecurity.payload.response.EmployeeResponse;
import com.example.springbootpostgressecurity.payload.response.EmployeeSalaryWindowResponse;
import com.example.springbootpostgressecurity.repository.DepartmentRepository;
import com.example.springbootpostgressecurity.repository.EmployeeRepository;
import com.example.springbootpostgressecurity.repository.ProjectAssignmentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            ProjectAssignmentRepository projectAssignmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.projectAssignmentRepository = projectAssignmentRepository;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll(Sort.by("id")).stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(Integer id) {
        return EmployeeResponse.from(getById(id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findByDepartmentId(Integer departmentId) {
        return employeeRepository.findByDepartment_Id(departmentId).stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeSalaryWindowResponse> findSalaryWindowsWithNativeSql() {
        return employeeRepository.findSalaryWindowsWithNativeSql().stream()
                .map(EmployeeSalaryWindowResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeSalaryWindowResponse> findSalaryWindowsWithJpql() {
        return employeeRepository.findSalaryWindowsWithJpql().stream()
                .map(EmployeeSalaryWindowResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeDepartmentAverageResponse> findEmployeesWithSalaryAboveDepartmentAverage() {
        return employeeRepository.findEmployeesWithSalaryAboveDepartmentAverage().stream()
                .map(EmployeeDepartmentAverageResponse::from)
                .toList();
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        Integer id = requireCreateId(request.getId());

        if (employeeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee id is already taken");
        }

        Employee employee = new Employee();
        employee.setId(id);
        employee.setFullName(request.getFullName());
        employee.setDepartment(resolveDepartment(request.getDepartmentId()));
        employee.setSalary(request.getSalary());

        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse update(Integer id, EmployeeRequest request) {
        Employee employee = getById(id);

        employee.setFullName(request.getFullName());
        employee.setDepartment(resolveDepartment(request.getDepartmentId()));
        employee.setSalary(request.getSalary());

        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional
    public void delete(Integer id) {
        Employee employee = getById(id);

        if (projectAssignmentRepository.existsByEmployee_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee has related project assignments");
        }

        employeeRepository.delete(employee);
    }

    private Employee getById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private Department resolveDepartment(Integer departmentId) {
        if (departmentId == null) {
            return null;
        }

        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
    }

    private Integer requireCreateId(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee id is required");
        }

        return id;
    }
}
