package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.RoleRepository;
import com.sira.rueng.ecommerce.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    // Test findAll
    @Test
    void testFindAll() {
        Role r1 = new Role();
        r1.setId(1);
        r1.setName("USER");

        Role r2 = new Role();
        r2.setId(2);
        r2.setName("ADMIN");

        when(roleRepository.findAll()).thenReturn(List.of(r1, r2));

        List<Role> result = roleService.findAll();

        assertEquals(2, result.size());
        assertEquals("USER", result.get(0).getName());
        assertEquals("ADMIN", result.get(1).getName());
    }

    // Test findById - Found
    @Test
    void testFindById_Found() {
        Role role = new Role();
        role.setId(1);
        role.setName("USER");

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.findById(1);
        assertTrue(result.isPresent());
        assertEquals("USER", result.get().getName());
    }

    // Test findById - Not Found
    @Test
    void testFindById_NotFound() {
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.findById(1);
        assertFalse(result.isPresent());
    }

    // Test findByName - Found
    @Test
    void testFindByName_Found() {
        Role role = new Role();
        role.setName("ADMIN");

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.findByName("ADMIN");
        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
    }

    // Test findByName - Not Found
    @Test
    void testFindByName_NotFound() {
        when(roleRepository.findByName("MANAGER")).thenReturn(Optional.empty());

        Optional<Role> result = roleService.findByName("MANAGER");
        assertFalse(result.isPresent());
    }

    // Test saveRole
    @Test
    void testSaveRole() {
        Role role = new Role();
        role.setName("NEW_ROLE");

        when(roleRepository.save(role)).thenReturn(role);

        Role result = roleService.saveRole(role);

        assertEquals("NEW_ROLE", result.getName());
        verify(roleRepository, times(1)).save(role);
    }
}
