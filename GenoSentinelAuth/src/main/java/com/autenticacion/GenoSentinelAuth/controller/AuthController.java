package com.autenticacion.GenoSentinelAuth.controller;

import com.autenticacion.GenoSentinelAuth.exceptions.InvalidEmailException;
import com.autenticacion.GenoSentinelAuth.exceptions.InvalidInputException;
import com.autenticacion.GenoSentinelAuth.exceptions.UserNotFoundException;
import com.autenticacion.GenoSentinelAuth.exceptions.IncorrectPasswordException;

import com.autenticacion.GenoSentinelAuth.models.dto.RegisterRequest;
import com.autenticacion.GenoSentinelAuth.models.entities.Role;
import com.autenticacion.GenoSentinelAuth.models.entities.Users;
import com.autenticacion.GenoSentinelAuth.repositories.RoleRepository;
import com.autenticacion.GenoSentinelAuth.repositories.UsersRepository;

import com.autenticacion.GenoSentinelAuth.services.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UsersRepository usuarioRepo;
    private final RoleRepository rolRepo;
    private final JwtService jwt;
    private final PasswordEncoder passwordEncoder;


    //LOGIN
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {

        String username = req.get("username");
        String email    = req.get("email");
        String password = req.get("password");

        // -------- VALIDACIONES --------

        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }

        // Buscar usuario
        Users user = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));

        // Validar email
        if (!email.equals(user.getEmail())) {
            throw new InvalidEmailException("Email does not match registered account");
        }

        // Validar contraseña mediante Spring Security
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (Exception e) {
            throw new IncorrectPasswordException("Incorrect password");
        }

        // Rol del usuario
        String roleName = user.getRole().getName();
        List<String> roles = List.of(roleName);

        // Crear token
        String token = jwt.generate(user.getUsername(), roles);

        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "roles", roles
        );
    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@RequestBody RegisterRequest req) {



        if (req.getUsername() == null || req.getUsername().trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty");
        }

        if (req.getPassword() == null || req.getPassword().trim().isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }

        if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty");
        }

        // Usuario duplicado
        if (usuarioRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new InvalidInputException("Username already exists");
        }

        // Rol USER
        Role roleUser = rolRepo.findByName("USER")
                .orElseThrow(() -> new UserNotFoundException("Role USER not found"));

        // Crear usuario
        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(roleUser);

        usuarioRepo.save(user);

        List<String> roles = List.of(roleUser.getName());
        String token = jwt.generate(user.getUsername(), roles);

        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "roles", roles
        );
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public Map<String, String> onAuthError(Exception e) {
        return Map.of("error", "Bad credentials");
    }
}
