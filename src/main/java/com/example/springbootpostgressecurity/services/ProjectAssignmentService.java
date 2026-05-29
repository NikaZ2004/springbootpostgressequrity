package com.example.springbootpostgressecurity.services;

import com.example.springbootpostgressecurity.models.Employee;
import com.example.springbootpostgressecurity.models.Project;
import com.example.springbootpostgressecurity.models.ProjectAssignment;
import com.example.springbootpostgressecurity.payload.request.ProjectAssignmentRequest;
import com.example.springbootpostgressecurity.payload.response.ProjectAssignmentDetailsResponse;
import com.example.springbootpostgressecurity.payload.response.ProjectAssignmentProjectSummaryResponse;
import com.example.springbootpostgressecurity.payload.response.ProjectAssignmentResponse;
import com.example.springbootpostgressecurity.repository.EmployeeRepository;
import com.example.springbootpostgressecurity.repository.ProjectAssignmentRepository;
import com.example.springbootpostgressecurity.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjectAssignmentService {
    private static final Logger log = LoggerFactory.getLogger(ProjectAssignmentService.class);

    private final ProjectAssignmentRepository projectAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    public ProjectAssignmentService(
            ProjectAssignmentRepository projectAssignmentRepository,
            EmployeeRepository employeeRepository,
            ProjectRepository projectRepository) {
        this.projectAssignmentRepository = projectAssignmentRepository;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectAssignmentResponse> findAll() {
        return projectAssignmentRepository.findAll(Sort.by("id")).stream()
                .map(ProjectAssignmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectAssignmentResponse findById(Integer id) {
        return ProjectAssignmentResponse.from(getById(id));
    }

    @Transactional(readOnly = true)
    public List<ProjectAssignmentResponse> findByEmployeeId(Integer employeeId) {
        return projectAssignmentRepository.findByEmployee_Id(employeeId).stream()
                .map(ProjectAssignmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectAssignmentResponse> findByProjectId(Integer projectId) {
        return projectAssignmentRepository.findByProject_Id(projectId).stream()
                .map(ProjectAssignmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectAssignmentDetailsResponse> findAssignmentDetailsJoin() {
        return projectAssignmentRepository.findAssignmentDetailsJoin().stream()
                .map(ProjectAssignmentDetailsResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectAssignmentProjectSummaryResponse> groupAssignmentsByProject() {
        return projectAssignmentRepository.groupAssignmentsByProject().stream()
                .map(ProjectAssignmentProjectSummaryResponse::from)
                .toList();
    }

    @Transactional
    public ProjectAssignmentResponse create(ProjectAssignmentRequest request) {
        Integer id = requireCreateId(request.getId());

        if (projectAssignmentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Project assignment id is already taken");
        }

        ProjectAssignment assignment = new ProjectAssignment();
        assignment.setId(id);
        updateAssignment(assignment, request);

        ProjectAssignment savedAssignment = projectAssignmentRepository.save(assignment);
        log.info("projectAssignment saved id {} employeeId {} projectId {}", savedAssignment.getId(),
                request.getEmployeeId(), request.getProjectId());
        return ProjectAssignmentResponse.from(savedAssignment);
    }

    @Transactional
    public ProjectAssignmentResponse update(Integer id, ProjectAssignmentRequest request) {
        ProjectAssignment assignment = getById(id);
        updateAssignment(assignment, request);

        ProjectAssignment savedAssignment = projectAssignmentRepository.save(assignment);
        log.info("projectAssignment updated id {} employeeId {} projectId {}", savedAssignment.getId(),
                request.getEmployeeId(), request.getProjectId());
        return ProjectAssignmentResponse.from(savedAssignment);
    }

    @Transactional
    public void delete(Integer id) {
        ProjectAssignment assignment = getById(id);
        projectAssignmentRepository.delete(assignment);
        log.info("projectAssignment deleted id {}", assignment.getId());
    }

    private ProjectAssignment getById(Integer id) {
        return projectAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project assignment not found"));
    }

    private void updateAssignment(ProjectAssignment assignment, ProjectAssignmentRequest request) {
        assignment.setEmployee(resolveEmployee(request.getEmployeeId()));
        assignment.setProject(resolveProject(request.getProjectId()));
        assignment.setRole(request.getRole());
        assignment.setWeeklyHours(request.getWeeklyHours());
    }

    private Employee resolveEmployee(Integer employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private Project resolveProject(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    private Integer requireCreateId(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project assignment id is required");
        }

        return id;
    }
}
