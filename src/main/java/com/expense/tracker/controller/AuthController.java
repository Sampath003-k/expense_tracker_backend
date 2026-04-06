package com.expense.tracker.controller;

import com.expense.tracker.dto.LoginRequest;
import com.expense.tracker.dto.LoginResponse;
import com.expense.tracker.dto.RegisterRequest;
import com.expense.tracker.model.User;
import com.expense.tracker.repository.UserRepository;
import com.expense.tracker.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")   // ✅ correct path
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword()));
        } catch (AuthenticationException ex) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid username or password"));
        }

        String token = jwtUtil.generateToken(request.getUsername());

        return ResponseEntity.ok(
            new LoginResponse(token, request.getUsername(), "Login successful"));
    }

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {

            return ResponseEntity
                .badRequest()
                .body(Map.of("message", "Username and password are required"));
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Username already exists"));
        }

        User user = new User(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(Map.of("message", "User registered successfully"));
    }
}