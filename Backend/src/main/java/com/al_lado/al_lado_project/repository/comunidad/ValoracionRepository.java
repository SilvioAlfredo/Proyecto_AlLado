package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de valoraciones entre empleadores y trabajadores.
 *
 * <p>Las agregaciones ({@link #calcularPromedioPuntuacionPorTrabajadorId(Long)} y
 * {@link #countByTrabajadorId(Long)}) alimentan las estadísticas del trabajador.</p>
 */
@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    List<Valoracion> findByTrabajadorId(Long trabajadorId);

    List<Valoracion> findByAutorId(Long autorId);

    @EntityGraph(attributePaths = {"autor", "trabajador"})
    Page<Valoracion> findByTrabajadorId(Long trabajadorId, Pageable pageable);

    @EntityGraph(attributePaths = {"autor", "trabajador"})
    Page<Valoracion> findByAutorId(Long autorId, Pageable pageable);

    /** @return {@code true} si esa pareja autor/trabajador ya se valoró (regla de una valoración por pareja). */
    boolean existsByAutorIdAndTrabajadorId(Long autorId, Long trabajadorId);

    /**
     * Cantidad de valoraciones recibidas por el trabajador, sin importar la
     * puntuación ni quién la emitió.
     */
    Long countByTrabajadorId(@Param("trabajadorId") Long trabajadorId);

    /**
     * Promedio de puntuación (1–5) de las valoraciones del trabajador.
     *
     * <p>La query <b>no tiene COALESCE</b>: si el trabajador no tiene valoraciones
     * devuelve {@code null}, no 0. Quien consume esto
     * ({@code PostulacionServiceImpl}) es el que lo sustituye por cero, de modo que
     * un promedio de 0 en las estadísticas significa «sin valoraciones», no
     * «valoración mínima».</p>
     *
     * @param trabajadorId ID del trabajador valorado
     * @return promedio o {@code null} si no hay valoraciones
     */
    @Query("SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.trabajador.id = :trabajadorId")
    Double calcularPromedioPuntuacionPorTrabajadorId(@Param("trabajadorId") Long trabajadorId);
}