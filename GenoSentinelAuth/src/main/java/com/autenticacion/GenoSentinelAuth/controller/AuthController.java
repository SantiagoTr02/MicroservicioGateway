package com.autenticacion.GenoSentinelAuth.controller;

import com.autenticacion.GenoSentinelAuth.services.JwtService;
import com.autenticacion.GenoSentinelAuth.exceptions.InvalidEmailException;
import com.autenticacion.GenoSentinelAuth.models.dto.RegisterRequest;
import com.autenticacion.GenoSentinelAuth.models.entities.Role;
import com.autenticacion.GenoSentinelAuth.models.entities.Users;
import com.autenticacion.GenoSentinelAuth.repositories.RoleRepository;
import com.autenticacion.GenoSentinelAuth.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    // ========= LOGIN =========
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String email    = req.get("email");
        String password = req.get("password");

        // Autenticar credenciales
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        Users user = usuarioRepo.findByUsername(username).orElseThrow();

        // Validar correo
        if (email == null || !email.equals(user.getEmail())) {
            throw new InvalidEmailException("El correo electrónico no coincide con el registrado.");
        }

        // Un solo rol
        String roleName = user.getRole().getName();
        List<String> roles = List.of(roleName);

        String token = jwt.generate(user.getUsername(), roles);

        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "roles", roles
        );
    }

    // ========= REGISTER =========
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@RequestBody RegisterRequest req) {

        if (req.getUsername() == null || req.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Missing username or password");
        }

        if (usuarioRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username already exists");
        }

        // Todos los usuarios tendrán el rol USER
        Role roleUser = rolRepo.findByName("USER")
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Role 'USER' not found")
                );

        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(roleUser); // <-- AQUÍ EL CAMBIO CLAVE

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
