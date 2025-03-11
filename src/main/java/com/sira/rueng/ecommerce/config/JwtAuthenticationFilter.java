package com.sira.rueng.ecommerce.config;

import com.sira.rueng.ecommerce.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.apache.bcel.generic.ClassGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("Startttttttttttttttttttttttttttttttttttttttttttt");
        // Log Request URL and HTTP method
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Request Method: " + request.getMethod());

        // Log Headers
        System.out.println("Request Headers: " + Collections.list(request.getHeaderNames()));

        // ✅ 1. อ่าน JWT จาก Cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> jwtCookie = Arrays.stream(cookies)
                    .filter(cookie -> "token".equals(cookie.getName()))
                    .findFirst();

            if (jwtCookie.isPresent()) {
                String token = jwtCookie.get().getValue();
                System.out.println("✅ มี Cookie Token: " + token);

                try {
                    // ✅ 2. ตรวจสอบ JWT และดึง Claims
                    Claims claims = jwtUtil.extractAllClaims(token);
                    String username = claims.getSubject();
                    String role = claims.get("role", String.class);

                    // ✅ 3. ตั้งค่า Authentication
                    System.out.println("THe role is : " + role);
                    Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));
//                    User userDetails = new User(username, "", authorities);
                    User userDetails = new User(username, "", Collections.singleton(() -> "ROLE_" + role.toLowerCase()));
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    System.out.println("authentication value: " + authentication);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("✅ Authenticated User: " + username + " | Role: " + role);
                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                    System.out.println("❌ Token ไม่ถูกต้อง: " + e.getMessage());
                }
            } else {
                System.out.println("❌ ไม่มี Cookie Token");
            }
        }

        // ✅ 4. ถ้าไม่มี Token → ตั้งค่าให้เป็น "guest" พร้อม Role "customer"
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            User guestUser = new User("guest", "", Collections.singleton(() -> "ROLE_CUSTOMER"));
            UsernamePasswordAuthenticationToken guestAuth =
                    new UsernamePasswordAuthenticationToken(guestUser, null, guestUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(guestAuth);
            System.out.println("⚠️ ตั้งค่า Guest User (customer)");
        }

        filterChain.doFilter(request, response);
    }
}
