package com.expense.tracker.repository;

import com.expense.tracker.model.Project;
import com.expense.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(String status);

    Boolean existsByName(String name);

    // ✅ ADD THIS (VERY IMPORTANT)
    List<Project> findByUser(User user);
}