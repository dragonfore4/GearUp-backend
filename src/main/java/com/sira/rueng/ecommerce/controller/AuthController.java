package com.sira.rueng.ecommerce.controller;

import com.sira.rueng.ecommerce.model.User;
import com.sira.rueng.ecommerce.response.ErrorResponse;
import com.sira.rueng.ecommerce.service.UserService;
import com.sira.rueng.ecommerce.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.apache.bcel.generic.ClassGen;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User userInput) {
        try {
            System.out.println(userInput);
            // Validate input
            if (userInput == null ||
                    StringUtils.isEmpty(userInput.getUsername()) ||
                    StringUtils.isEmpty(userInput.getEmail()) ||
                    StringUtils.isEmpty(userInput.getPassword())) {
                ErrorResponse error = new ErrorResponse("Invalid user data", 399);
                return ResponseEntity.badRequest().body(error);
            }

            // Perform registration
            User registeredUser = userService.register(userInput);
            // Response
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Registered  successfully");


            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), 399));
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Registration failed " + e.getMessage(), 499);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest, HttpServletResponse response) {
        try {
            if (loginRequest == null ||
                    StringUtils.isEmpty(loginRequest.getUsername()) ||
                    StringUtils.isEmpty(loginRequest.getPassword())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid login data", 400));
            }

            // Login และรับ JWT
            String jwt = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

            if (jwt == null) { // If JWT is null, return UNAUTHORIZED
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid username or password", 401));
            }
            // ตั้งค่า Cookie
            Cookie cookie = new Cookie("token", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            cookie.setAttribute("SameSite", "Lax");
            cookie.setSecure(false); // เปลี่ยนเป็น true ถ้าใช้ HTTPS
            response.addCookie(cookie);

            // Response
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Logged in successfully");
            responseBody.put("token", jwt);

            return ResponseEntity.ok(responseBody);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage(), 401));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // ลบ Cookie
        Cookie cookie = new Cookie("token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(Collections.singletonMap("message", "Logged out successfully"));
    }

    @GetMapping("/getToken")
    public ResponseEntity<?> getToken(HttpServletRequest request) {
        System.out.println("entry");
        Cookie[] cookies = request.getCookies();

        // ✅ Prevent NullPointerException
        if (cookies == null || cookies.length == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No cookies found", 400));
        }

        // Print all cookies for debugging
        for (Cookie cookie : cookies) {
            System.out.println("Cookie Name: " + cookie.getName() + ", Value: " + cookie.getValue());
        }

        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                System.out.println("found");
                String token = cookie.getValue();
                System.out.println(token);
                return ResponseEntity.ok(Collections.singletonMap("token", token));
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Token not found", 404));
    }



    @GetMapping("/getAllClaims")
    public ResponseEntity<?> getAllClaims(@RequestHeader("Authorization") String token) {
//        System.out.println("in this get all claims" + token);
        try {
            // ✅ เอา "Bearer " ออกก่อน
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // ✅ ดึง Claims จาก Token
            Claims claims = jwtUtil.extractAllClaims(token);
//            System.out.println("this is token: " + token);

            return ResponseEntity.ok(claims);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid Token"));
        }
    }

}
