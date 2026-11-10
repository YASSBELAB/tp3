
package com.example.lab3.controller;

import com.example.lab3.dto.RegisterRequest;
import com.example.lab3.dto.LoginRequest;
import com.example.lab3.dto.JwtResponse;
import com.example.lab3.entity.User;
import com.example.lab3.repository.UserRepository;
import com.example.lab3.security.JwtUtil;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(
            UserRepository userRepo,
            PasswordEncoder encoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil) {

        this.userRepo = userRepo;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest req) {

        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        User user = new User();

        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));

        // New registrations receive USER role
        user.setRole("USER");

        userRepo.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest req) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getUsername(),
                            req.getPassword()
                    )
            );

            UserDetails user = (UserDetails) auth.getPrincipal();

            String role = user.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority()
                    .replace("ROLE_", "");

            String jwt = jwtUtil.generateToken(
                    user.getUsername(),
                    role
            );

            return ResponseEntity.ok(new JwtResponse(jwt));

        } catch (AuthenticationException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    // REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestHeader(value = "Authorization", required = false)
            String authorizationHeader) {

        // Check Authorization header
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid Authorization header");
        }

        // Extract token
        String token = authorizationHeader.substring(7);

        // Check token validity
        if (!jwtUtil.validateToken(token)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }

        // Check whether token is close to expiration
        if (!jwtUtil.isCloseToExpiration(token)) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token is not close to expiration");
        }

        // Extract username
        String username = jwtUtil.extractUsername(token);

        // Retrieve current user and role from database
        User user = userRepo.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        // Generate new token
        String newToken = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity.ok(new JwtResponse(newToken));
    }
}
