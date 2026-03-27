package com.expense.tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import com.expense.tracker.model.User;
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String projectName;

    @NotBlank
    @Column(nullable = false)
    private String expenseType;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double amount;

    @NotBlank
    @Column(nullable = false)
    private String vendor;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String description;

    // Optional FK link to projects table (nullable for backward compat)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = true)
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Default constructor
    public Expense() {}

    // Parameterized constructor
    public Expense(String projectName, String expenseType, Double amount,
                   String vendor, LocalDate date, String description) {
        this.projectName = projectName;
        this.expenseType = expenseType;
        this.amount = amount;
        this.vendor = vendor;
        this.date = date;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getExpenseType() { return expenseType; }
    public void setExpenseType(String expenseType) { this.expenseType = expenseType; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
