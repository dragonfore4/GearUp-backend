package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.CartRepository;
import com.sira.rueng.ecommerce.dao.RoleRepository;
import com.sira.rueng.ecommerce.dao.UserRepository;
import com.sira.rueng.ecommerce.model.Cart;
import com.sira.rueng.ecommerce.model.Role;
import com.sira.rueng.ecommerce.model.User;
import com.sira.rueng.ecommerce.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock CartRepository cartRepository;
    @Mock JwtUtil jwtUtil;

    @InjectMocks UserService userService;

    // Test findAll
    @Test
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
    }

    // Test findById - Found
    @Test
    void testFindById_Found() {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    // Test findById - Not Found
    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(1);
        assertFalse(result.isPresent());
    }

    // Test findByUsername
    @Test
    void testFindByUsername() {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("testuser");
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    // Test deleteUser
    @Test
    void testDeleteUser() {
        userService.deleteUser(1);
        verify(userRepository).deleteById(1);
    }

    // Test updateUser - Found
    @Test
    void testUpdateUser_Found() {
        User existing = new User();
        existing.setUsername("old");

        User updated = new User();
        updated.setUsername("new");

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(1, updated);

        assertEquals("new", result.getUsername());
    }

    // Test updateUser - Not Found
    @Test
    void testUpdateUser_NotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        User result = userService.updateUser(1, new User());
        assertNull(result);
    }

    // Test register - success
    @Test
    void testRegister_Success() {
        User user = new User();
        user.setUsername("test");
        user.setEmail("test@mail.com");
        user.setPassword("plain123");

        when(userRepository.existsByUsername("test")).thenReturn(false);
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);

        Role role = new Role("Customer");
        when(roleRepository.findByName("Customer")).thenReturn(Optional.of(role));

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setUsername("test");
        when(userRepository.save(any())).thenReturn(savedUser);

        Cart cart = new Cart();
        when(cartRepository.save(any())).thenReturn(cart);

        User result = userService.register(user);
        assertEquals("test", result.getUsername());
        verify(cartRepository).save(any());
    }

    // Test register - username exists
    @Test
    void testRegister_UsernameExists() {
        User user = new User();
        user.setUsername("taken");
        user.setEmail("new@mail.com");

        when(userRepository.existsByUsername("taken")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.register(user);
        });

        assertEquals("Username already exists", ex.getMessage());
    }

    // Test register - email exists
    @Test
    void testRegister_EmailExists() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("duplicate@mail.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("duplicate@mail.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.register(user);
        });

        assertEquals("Email already exists", ex.getMessage());
    }

    // Test login - success
    @Test
    void testLogin_Success() {
        User user = new User();
        user.setUsername("test");
        user.setPassword(new BCryptPasswordEncoder().encode("pass123"));
        user.setRole(new Role("Customer"));

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("test", "Customer")).thenReturn("mock-jwt");

        String token = userService.login("test", "pass123");

        assertEquals("mock-jwt", token);
    }

    // Test login - wrong password
    @Test
    void testLogin_WrongPassword() {
        User user = new User();
        user.setUsername("test");
        user.setPassword(new BCryptPasswordEncoder().encode("correctpass"));
        user.setRole(new Role("Customer"));

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        String result = userService.login("test", "wrongpass");
        assertNull(result);
    }

    // Test login - user not found
    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());

        String result = userService.login("nouser", "any");
        assertNull(result);
    }
}
