package com.sira.rueng.ecommerce.controller;

import com.sira.rueng.ecommerce.model.User;
import com.sira.rueng.ecommerce.response.ErrorResponse;
import com.sira.rueng.ecommerce.service.RoleService;
import com.sira.rueng.ecommerce.service.UserService;
import com.sira.rueng.ecommerce.utils.JwtUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;
    private RoleService roleService;
    private JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, RoleService roleService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.roleService = roleService;
        this.jwtUtil = jwtUtil;
    }




    // Read - Get all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // Get user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            // Return error message with HTTP 404
            ErrorResponse error = new ErrorResponse("User not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }


    // Read - Get user by username
    @GetMapping("/users/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> optionalUser = userService.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Update user
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            // Return error message if user is not found
            ErrorResponse error = new ErrorResponse("User not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}