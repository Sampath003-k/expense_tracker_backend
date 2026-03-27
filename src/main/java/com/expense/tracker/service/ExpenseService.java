package com.expense.tracker.service;

import com.expense.tracker.model.Expense;
import com.expense.tracker.model.User;
import com.expense.tracker.repository.ExpenseRepository;
import com.expense.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    // 🔥 Get current logged-in user
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ Get all expenses (role-based)
    public List<Expense> getAllExpenses() {
        User user = getCurrentUser();

        if (user.getRole().equals("ROLE_ADMIN")) {
            return expenseRepository.findAll();
        }

        return expenseRepository.findByUser(user); // ✅ FIXED
    }

    // ✅ Get expense by ID (secure)
    public Optional<Expense> getExpenseById(Long id) {
        User user = getCurrentUser();

        return expenseRepository.findById(id)
                .filter(e -> e.getUser().getId().equals(user.getId()));
    }

    // ✅ Search (user-based)
    public List<Expense> searchExpenses(String keyword) {
        User user = getCurrentUser();

        if (keyword == null || keyword.isBlank()) {
            return expenseRepository.findByUser(user);
        }

        return expenseRepository.findByUser(user).stream()
                .filter(e ->
                        e.getProjectName().toLowerCase().contains(keyword.toLowerCase()) ||
                        e.getVendor().toLowerCase().contains(keyword.toLowerCase()) ||
                        e.getExpenseType().toLowerCase().contains(keyword.toLowerCase())
                )
                .toList();
    }

    // ✅ Create expense (attach user)
    public Expense createExpense(Expense expense) {
        User user = getCurrentUser();
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    // ✅ Update (secure)
    public Expense updateExpense(Long id, Expense updatedExpense) {
        User user = getCurrentUser();

        return expenseRepository.findById(id)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .map(expense -> {
                    expense.setProjectName(updatedExpense.getProjectName());
                    expense.setExpenseType(updatedExpense.getExpenseType());
                    expense.setAmount(updatedExpense.getAmount());
                    expense.setVendor(updatedExpense.getVendor());
                    expense.setDate(updatedExpense.getDate());
                    expense.setDescription(updatedExpense.getDescription());
                    return expenseRepository.save(expense);
                })
                .orElseThrow(() -> new RuntimeException("Unauthorized or not found"));
    }

    // ✅ Delete (secure)
    public void deleteExpense(Long id) {
        User user = getCurrentUser();

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        expenseRepository.deleteById(id);
    }
}