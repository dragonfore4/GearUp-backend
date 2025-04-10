package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.CartRepository;
import com.sira.rueng.ecommerce.dao.RoleRepository;
import com.sira.rueng.ecommerce.dao.UserRepository;
import com.sira.rueng.ecommerce.model.Cart;
import com.sira.rueng.ecommerce.model.Role;
import com.sira.rueng.ecommerce.model.User;
import com.sira.rueng.ecommerce.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private CartRepository cartRepository;
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, CartRepository cartRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cartRepository = cartRepository;
        this.jwtUtil = jwtUtil;
    }


    // find All
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // find by id
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    // find by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // delete
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    // Update
    public User updateUser(Integer id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(userDetails.getUsername());
            return userRepository.save(user);
        }
        return null; // Or throw exception if user not found
    }

    // register
    @Transactional
    public User register(User user) {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new RuntimeException("Username already exists");
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setId(null);

            Optional<Role> optionalRole = roleRepository.findByName("Customer");
            Role role;
            if (optionalRole.isPresent()) {
                role = optionalRole.get();
            } else {
                role = new Role("Customer");
                roleRepository.save(role);
            }

            user.setRole(role);

            user.setPassword(encoder.encode(user.getPassword()));

            User registeredUser = userRepository.save(user);

            Cart cart = new Cart();
            cart.setUser(registeredUser);
            cartRepository.save(cart);


            return registeredUser;
    }

    // login
    public String login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (encoder.matches(password, user.getPassword())) {
                // สร้าง jwt
                String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole().getName());

                return jwt;
            } else {
                return null;
            }
        }

        return null;
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
