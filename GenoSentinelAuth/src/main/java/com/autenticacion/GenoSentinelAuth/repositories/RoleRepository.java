package com.autenticacion.GenoSentinelAuth.repositories;

import com.autenticacion.GenoSentinelAuth.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Role.
 * Gestiona los roles de usuario y proporciona métodos para buscar roles por nombre o identificador.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
