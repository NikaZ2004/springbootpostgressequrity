package com.example.springbootpostgressecurity.repository;

import com.example.springbootpostgressecurity.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findByDepartment_Id(Integer departmentId);

    boolean existsByDepartment_Id(Integer departmentId);
}
