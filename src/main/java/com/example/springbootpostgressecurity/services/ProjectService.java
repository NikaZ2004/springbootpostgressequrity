package com.example.springbootpostgressecurity.services;

import com.example.springbootpostgressecurity.models.Department;
import com.example.springbootpostgressecurity.models.Project;
import com.example.springbootpostgressecurity.payload.request.ProjectRequest;
import com.example.springbootpostgressecurity.payload.response.ProjectResponse;
import com.example.springbootpostgressecurity.repository.DepartmentRepository;
import com.example.springbootpostgressecurity.repository.ProjectAssignmentRepository;
import com.example.springbootpostgressecurity.repository.ProjectRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            DepartmentRepository departmentRepository,
            ProjectAssignmentRepository projectAssignmentRepository) {
        this.projectRepository = projectRepository;
        this.departmentRepository = departmentRepository;
        this.projectAssignmentRepository = projectAssignmentRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll(Sort.by("id")).stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Integer id) {
        return ProjectResponse.from(getById(id));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findByDepartmentId(Integer departmentId) {
        return projectRepository.findByDepartment_Id(departmentId).stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Integer id = requireCreateId(request.getId());

        if (projectRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Project id is already taken");
        }

        Project project = new Project();
        project.setId(id);
        project.setName(request.getName());
        project.setDepartment(resolveDepartment(request.getDepartmentId()));
        project.setBudget(request.getBudget());

        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse update(Integer id, ProjectRequest request) {
        Project project = getById(id);

        project.setName(request.getName());
        project.setDepartment(resolveDepartment(request.getDepartmentId()));
        project.setBudget(request.getBudget());

        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public void delete(Integer id) {
        Project project = getById(id);

        if (projectAssignmentRepository.existsByProject_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Project has related assignments");
        }

        projectRepository.delete(project);
    }

    private Project getById(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project id is required");
        }

        return id;
    }
}
