package com.autenticacion.GenoSentinelAuth.repositories;

import com.autenticacion.GenoSentinelAuth.models.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Users.
 * Administra la persistencia de los usuarios del sistema, incluyendo búsquedas por nombre o correo.
 */
public interface UsersRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByUsername(String username);
}
