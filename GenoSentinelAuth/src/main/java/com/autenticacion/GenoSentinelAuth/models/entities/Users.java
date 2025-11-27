package com.autenticacion.GenoSentinelAuth.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entidad que representa un usuario del sistema.
 * Cada usuario tendrá un solo rol asignado.
 */
@Entity
@Table(name = "users")
@Data
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    private Boolean active = true;

    // Relación con el rol de usuario (muchos a uno con la tabla roles)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role; // Un usuario tiene un solo rol

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // El rol del usuario se asigna como la única autoridad
        return List.of(() -> "ROLE_" + role.getName());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return active; }
}
