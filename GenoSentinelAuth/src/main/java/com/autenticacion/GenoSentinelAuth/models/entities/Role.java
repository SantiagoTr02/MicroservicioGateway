package com.autenticacion.GenoSentinelAuth.models.entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa un rol de usuario dentro del sistema.
 * Los roles determinan los permisos de acceso y se utilizan en la autenticación
 * y autorización mediante JWT.
 * Se mapea a la tabla "roles".
 */
@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // ADMIN, USER, etc.
}