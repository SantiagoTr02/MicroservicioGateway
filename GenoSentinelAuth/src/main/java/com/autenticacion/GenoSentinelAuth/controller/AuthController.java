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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Authentication", description = "Endpoints para autenticación y registro de usuarios")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UsersRepository usuarioRepo;
    private final RoleRepository rolRepo;
    private final JwtService jwt;
    private final PasswordEncoder passwordEncoder;

    // ------------------------------
    // LOGIN
    // ------------------------------

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario mediante username, email y password. "
                    + "Devuelve un JWT para usar en otros microservicios."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
            @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {

        String username = req.get("username");
        String email    = req.get("email");
        String password = req.get("password");

        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }

        Users user = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));

        if (!email.equals(user.getEmail())) {
            throw new InvalidEmailException("Email does not match registered account");
        }

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (Exception e) {
            throw new IncorrectPasswordException("Incorrect password");
        }

        List<String> roles = List.of(user.getRole().getName());
        String token = jwt.generate(user.getUsername(), roles);

        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "roles", roles
        );
    }

    // ------------------------------
    // REGISTER
    // ------------------------------

    @Operation(
            summary = "Registrar un usuario",
            description = "Crea un nuevo usuario en el sistema con el rol USER por defecto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rol USER no encontrado", content = @Content)
    })
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

        if (usuarioRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new InvalidInputException("Username already exists");
        }

        Role roleUser = rolRepo.findByName("USER")
                .orElseThrow(() -> new UserNotFoundException("Role USER not found"));

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


    // Manejador de errores de autenticación
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public Map<String, String> onAuthError(Exception e) {
        return Map.of("error", "Bad credentials");
    }
}
