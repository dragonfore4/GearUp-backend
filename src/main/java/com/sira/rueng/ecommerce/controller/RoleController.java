package com.sira.rueng.ecommerce.controller;

import com.sira.rueng.ecommerce.model.Role;
import com.sira.rueng.ecommerce.response.ErrorResponse;
import com.sira.rueng.ecommerce.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RoleController {

    private RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    public List<Role> getAllUser() {
        return roleService.findAll();
    }



    @GetMapping("/roles/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        Optional<Role> role = roleService.findById(id);

        if (role.isPresent()) {
              return ResponseEntity.ok(role.get());
        } else {
            ErrorResponse errorResponse = new ErrorResponse("role not found with id: " + id, HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
