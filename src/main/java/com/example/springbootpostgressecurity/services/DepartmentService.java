package com.example.springbootpostgressecurity.services;

import com.example.springbootpostgressecurity.models.Department;
import com.example.springbootpostgressecurity.payload.request.DepartmentRequest;
import com.example.springbootpostgressecurity.payload.response.DepartmentEmployeeResponse;
import com.example.springbootpostgressecurity.payload.response.DepartmentResponse;
import com.example.springbootpostgressecurity.repository.DepartmentRepository;
import com.example.springbootpostgressecurity.repository.EmployeeRepository;
import com.example.springbootpostgressecurity.repository.ProjectRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    public DepartmentService(
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository,
            ProjectRepository projectRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll(Sort.by("id")).stream()
                .map(DepartmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse findById(Integer id) {
        return DepartmentResponse.from(getById(id));
    }

    @Transactional(readOnly = true)
    public List<DepartmentEmployeeResponse> findDepartmentsWithEmployeesLeftJoin() {
        return departmentRepository.findDepartmentsWithEmployeesLeftJoin().stream()
                .map(DepartmentEmployeeResponse::from)
                .toList();
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        Integer id = requireCreateId(request.getId());

        if (departmentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department id is already taken");
        }
        if (departmentRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department name is already taken");
        }

        Department department = new Department();
        department.setId(id);
        department.setName(request.getName());

        return DepartmentResponse.from(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentResponse update(Integer id, DepartmentRequest request) {
        Department department = getById(id);

        departmentRepository.findByName(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Department name is already taken");
                });

        department.setName(request.getName());

        return DepartmentResponse.from(departmentRepository.save(department));
    }

    @Transactional
    public void delete(Integer id) {
        Department department = getById(id);

        if (employeeRepository.existsByDepartment_Id(id) || projectRepository.existsByDepartment_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Department has related employees or projects");
        }

        departmentRepository.delete(department);
    }

    private Department getById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
    }

    private Integer requireCreateId(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department id is required");
        }

        return id;
    }
}
