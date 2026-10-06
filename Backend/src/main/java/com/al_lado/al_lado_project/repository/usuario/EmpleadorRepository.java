package com.al_lado.al_lado_project.repository.usuario;

import com.al_lado.al_lado_project.entity.usuario.Empleador;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpleadorRepository extends JpaRepository<Empleador, Long> {

    Optional<Empleador> findByEmail(String email);

    // Bloqueo pesimista para serializar creaciones concurrentes del mismo
    // empleador (p. ej. control de limite de ofertas): el lock se retiene
    // hasta el commit de la transaccion que lo adquirio.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Empleador e WHERE e.id = :id")
    Optional<Empleador> findByIdForUpdate(@Param("id") Long id);
}