package com.sira.rueng.ecommerce.config;

import com.sira.rueng.ecommerce.model.User;
import com.sira.rueng.ecommerce.service.UserService;
import com.sira.rueng.ecommerce.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtUtil jwtUtil, UserService userService ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // products
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole("admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("admin")
                        // auth
                        .requestMatchers("/api/auth/**").permitAll()
                        // users
//                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("admin", "customer")
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasAnyRole("admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("admin")
                        .requestMatchers("/api/orders").hasAnyRole("customer","admin")
                        // orders
                                .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("admin", "customer")
                                .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyRole("admin","customer")
                                .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasRole("admin")
                                .requestMatchers(HttpMethod.DELETE, "/api/order/**").hasRole("admin")
                        // cart-detail
                        .requestMatchers("/api/cartDetail/**").permitAll()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

                            String email = oauthUser.getAttribute("email");
                            String username = oauthUser.getAttribute("name") != null ? oauthUser.getAttribute("name") : oauthUser.getAttribute("login");
                            String avatar = oauthUser.getAttribute("picture") != null ? oauthUser.getAttribute("picture") : oauthUser.getAttribute("avatar_url");

                            Optional<User> existingUser = userService.findByEmail(email);
                            User user;

                            if (existingUser.isPresent()) {
                                user = existingUser.get();
                                System.out.println("✅ Logged in existing user: " + email);
                            } else {
                                System.out.println("🆕 Registering new user: " + email);
                                user = new User();
                                user.setEmail(email);
                                user.setUsername(username);
                                user.setPassword(UUID.randomUUID().toString()); // dummy password
                                user = userService.register(user);
                            }

                            // ✅ Create JWT
                            String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole().getName());

                            // 🍪 Set cookie (optional)
                            Cookie cookie = new Cookie("token", jwt);
                            cookie.setHttpOnly(true);
                            cookie.setPath("/");
                            cookie.setMaxAge(3600);
                            cookie.setAttribute("SameSite", "None");
                            cookie.setSecure(true);
                            response.addCookie(cookie);

//                            response.sendRedirect("http://localhost:3000/");
                            response.sendRedirect("http://localhost:3000/");
                        })
                )
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
