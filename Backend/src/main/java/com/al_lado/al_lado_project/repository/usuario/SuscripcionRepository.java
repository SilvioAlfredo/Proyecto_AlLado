package com.al_lado.al_lado_project.repository.usuario;

import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de suscripciones.
 *
 * <p>Los criterios de <b>vigencia</b> aquí no son los que aplica el negocio: la
 * comprobación de un plan PRO vigente vive en
 * {@code SuscripcionServiceImpl#esProVigente}. Los métodos de esta interfaz
 * aplican los criterios específicos documentados en cada consulta.</p>
 */
@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long>, JpaSpecificationExecutor<Suscripcion> {

    @EntityGraph(attributePaths = {"usuario"})
    Page<Suscripcion> findAll(Specification<Suscripcion> specification, Pageable pageable);

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
    /**
     * Suscripciones activas vencidas antes de la fecha indicada ({@code fechaFin <
     * :fecha}, estricto). Para comprobar PRO vigente, el negocio utiliza
     * {@code SuscripcionServiceImpl#esProVigente} comparando contra hoy.
     */
    List<Suscripcion> findByActivaTrueAndFechaFinBefore(LocalDate fecha);

    /**
     * Suscripción vigente de un usuario: {@code activa}, {@code fechaInicio ≤ hoy},
     * y {@code fechaFin} nula o {@code ≥ hoy} (incluye ambas fechas). No filtra por nivel.
     *
     * <p>La consulta no exige nivel PRO; esa comprobación adicional corresponde
     * a {@code SuscripcionServiceImpl#esProVigente}.</p>
     *
     * @param usuarioId ID del usuario
     * @param hoy       fecha de referencia
     */
    @Query("SELECT s FROM Suscripcion s WHERE s.usuario.id = :usuarioId AND s.activa = true AND s.fechaInicio <= :hoy AND (s.fechaFin IS NULL OR s.fechaFin >= :hoy)")
    Optional<Suscripcion> findSuscripcionVigentePorUsuarioId(@Param("usuarioId") Long usuarioId, @Param("hoy") LocalDate hoy);

    /**
     * Cantidad de suscripciones PRO vigentes para una fecha: activa, iniciada y no vencida.
     * Base del cálculo estimado de ingresos del dashboard (15.000 por unidad).
     *
     * <p>Usada por {@code AdministradorServiceImpl#obtenerMetricasDashboard}.</p>
     */
    @Query("SELECT COUNT(s) FROM Suscripcion s WHERE s.activa = true AND UPPER(s.nivel) = 'PRO' AND s.fechaInicio <= :hoy AND (s.fechaFin IS NULL OR s.fechaFin >= :hoy)")
    long countProVigentes(@Param("hoy") LocalDate hoy);
}