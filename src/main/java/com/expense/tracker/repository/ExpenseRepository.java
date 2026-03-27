package com.expense.tracker.repository;

import com.expense.tracker.model.Expense;
import com.expense.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE " +
           "LOWER(e.projectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.vendor) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.expenseType) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Expense> searchByKeyword(@Param("keyword") String keyword);

    List<Expense> findByProjectNameIgnoreCase(String projectName);

    // ✅ ADD THIS
    List<Expense> findByUser(User user);
}