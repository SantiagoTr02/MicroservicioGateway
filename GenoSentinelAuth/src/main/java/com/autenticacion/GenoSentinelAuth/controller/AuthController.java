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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controlador para la autenticación y registro de usuarios.
 * Proporciona endpoints para login y registro, así como manejo de errores de autenticación.
 * Utiliza JWT para la generación de tokens y roles para la autorización.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    /**
     * AuthenticationManager de Spring Security para autenticar usuarios.
     */
    private final AuthenticationManager authManager;
    /**
     * Repositorio para la gestión de usuarios.
     */
    private final UsersRepository usuarioRepo;
    /**
     * Repositorio para la gestión de roles.
     */
    private final RoleRepository rolRepo;
    /**
     * Servicio para la gestión de JWT.
     */
    private final JwtService jwt;
    /**
     * PasswordEncoder para encriptar contraseñas.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Endpoint para el login de usuarios.
     * Autentica las credenciales y retorna un token JWT si son válidas.
     * @param req mapa con username y password
     * @return mapa con el token, tipo y roles
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String email = req.get("email");
        String password = req.get("password");

        // Si las credenciales son incorrectas, lanza AuthenticationException → 401 por defecto
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        var user = usuarioRepo.findByUsername(username).orElseThrow();
        var roles = user.getRoles().stream().map(Role::getName).toList();

        if (email == null || !email.equals(user.getEmail())) {
            throw new InvalidEmailException("El correo electrónico no coincide con el registrado.");
        }

        String token = jwt.generate(user.getUsername(), roles);

        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "roles", roles
        );
    }

    /**
     * Endpoint para el registro de nuevos usuarios.
     * Valida los datos, asigna roles y retorna un token JWT.
     * @param req datos de registro (username, password, roles)
     * @return mapa con el token, tipo y roles
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@RequestBody RegisterRequest req) {
        if (req.getUsername() == null || req.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing username or password");
        }
        if (usuarioRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        List<String> roleNames = (req.getRoles() == null || req.getRoles().isEmpty())
                ? List.of("USER")
                : req.getRoles();

        // Manejo correcto de roles para evitar ConcurrentModificationException
        Set<Role> roleEntities = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = rolRepo.findByName(roleName).orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName(roleName);
                return rolRepo.save(newRole);
            });
            roleEntities.add(role);
        }

        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRoles(roleEntities);

        usuarioRepo.save(user);

        List<String> roles = roleEntities.stream().map(Role::getName).toList();
        String token = jwt.generate(user.getUsername(), roles);

        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "roles", roles
        );
    }

    /**
     * Maneja errores de autenticación devolviendo un mensaje estándar.
     * @param e excepción de autenticación
     * @return mapa con el error
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public Map<String, String> onAuthError(Exception e) {
        return Map.of("error", "Bad credentials");
    }
}