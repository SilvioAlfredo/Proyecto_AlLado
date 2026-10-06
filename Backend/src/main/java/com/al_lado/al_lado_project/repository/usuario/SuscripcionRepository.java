package com.al_lado.al_lado_project.repository.usuario;

import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    // Buscar la suscripción vinculada a un usuario específico
    Optional<Suscripcion> findByUsuario(Usuario usuario);

    // Buscar suscripción por el ID del usuario directamente
    Optional<Suscripcion> findByUsuarioId(Long usuarioId);

    // Verificar si un usuario ya tiene una suscripción registrada
    boolean existsByUsuarioId(Long usuarioId);

    // Listar todas las suscripciones activas/inactivas por su estado
    List<Suscripcion> findByActiva(boolean activa);

    // Listar suscripciones por nivel (ej. "BASICO", "PRO")
    List<Suscripcion> findByNivel(String nivel);

    // Obtener suscripciones activas pertenecientes a un nivel en particular
    List<Suscripcion> findByNivelAndActiva(String nivel, boolean activa);

    // Consultar suscripciones activas cuya fecha de vencimiento sea anterior o igual a una fecha limite (ideal para procesos de renovación/caducidad)
    List<Suscripcion> findByActivaTrueAndFechaFinBefore(LocalDate fecha);

    // Query JPQL para consultar si un usuario tiene una suscripción vigente al día de hoy
    @Query("SELECT s FROM Suscripcion s WHERE s.usuario.id = :usuarioId AND s.activa = true AND (s.fechaFin IS NULL OR s.fechaFin >= :hoy)")
    Optional<Suscripcion> findSuscripcionVigentePorUsuarioId(@Param("usuarioId") Long usuarioId, @Param("hoy") LocalDate hoy);

    // Agregacion en BD para el dashboard: cuenta sin cargar entidades.
    @Query("SELECT COUNT(s) FROM Suscripcion s WHERE s.activa = true AND UPPER(s.nivel) = 'PRO'")
    long countActivasPro();
}