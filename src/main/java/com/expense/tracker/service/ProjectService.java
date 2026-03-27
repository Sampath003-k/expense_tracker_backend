package com.expense.tracker.service;

import com.expense.tracker.model.Project;
import com.expense.tracker.model.User;
import com.expense.tracker.repository.ProjectRepository;
import com.expense.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    // 🔥 Get logged-in user
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ Get projects (user-based)
    public List<Project> getAllProjects() {
        User user = getCurrentUser();
        return projectRepository.findByUser(user);
    }

    // ✅ Get project by ID (secure)
    public Optional<Project> getProjectById(Long id) {
        User user = getCurrentUser();

        return projectRepository.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()));
    }

    // ✅ Create project (attach user)
    public Project createProject(Project project) {
        User user = getCurrentUser();

        // check duplicate only for same user
        List<Project> userProjects = projectRepository.findByUser(user);
        boolean exists = userProjects.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(project.getName()));

        if (exists) {
            throw new RuntimeException("Project already exists for this user");
        }

        project.setUser(user);
        return projectRepository.save(project);
    }

    // ✅ Update (secure)
    public Project updateProject(Long id, Project updated) {
        User user = getCurrentUser();

        return projectRepository.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .map(project -> {
                    project.setName(updated.getName());
                    project.setDescription(updated.getDescription());
                    project.setStatus(updated.getStatus());
                    return projectRepository.save(project);
                })
                .orElseThrow(() -> new RuntimeException("Unauthorized or not found"));
    }

    // ✅ Delete (secure)
    public void deleteProject(Long id) {
        User user = getCurrentUser();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        projectRepository.deleteById(id);
    }

    // ✅ Active projects (user-based)
    public List<Project> getActiveProjects() {
        User user = getCurrentUser();

        return projectRepository.findByUser(user).stream()
                .filter(p -> p.getStatus().equalsIgnoreCase("ACTIVE"))
                .toList();
    }
}