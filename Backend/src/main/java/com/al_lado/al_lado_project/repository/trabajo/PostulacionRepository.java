package com.al_lado.al_lado_project.repository.trabajo;

import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de postulaciones.
 *
 * <p>Además de los accesos por oferta/autor, expone las agregaciones que alimentan
 * las estadísticas del trabajador y el dashboard administrativo: todas se resuelven
 * en BD, sin cargar las postulaciones a memoria.</p>
 */
@Repository
public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {

    @EntityGraph(attributePaths = {"autor", "oportunidadLaboral"})
    Page<Postulacion> findByOportunidadLaboralId(Long oportunidadId, Pageable pageable);

    List<Postulacion> findByOportunidadLaboralId(Long oportunidadId);

    List<Postulacion> findByAutorId(Long autorId);

    Optional<Postulacion> findByOportunidadLaboralIdAndAutorId(Long oportunidadId, Long autorId);

    boolean existsByOportunidadLaboralIdAndAutorId(Long oportunidadId, Long autorId);

    /**
     * Cantidad de postulaciones del autor, <b>sin importar el estado</b>: es la base
     * de {@code postulacionesEnviadas} de las estadísticas del trabajador.
     */
    long countByAutorId(Long autorId);

    /**
     * Conteo de postulaciones del autor en un estado concreto, comparando el estado
     * sin distinguir mayúsculas (los estados se guardan en mayúsculas, pero la
     * comparación tolera variantes).
     */
    long countByAutorIdAndEstadoIgnoreCase(Long autorId, String estado);

    /**
     * Conteo global por estado para el dashboard; misma comparación
     * insensible a mayúsculas. Cuenta todas las postulaciones, sin filtro de fecha.
     */
    long countByEstadoIgnoreCase(String estado);

    /**
     * Promedio de {@code remuneracionPretendida} de las postulaciones con monto
     * estrictamente positivo ({@code > 0}). Es el <b>valor declarado</b> al postular,
     * no una remuneración
     * pagada; no distingue estado de la postulación.
     *
     * @return promedio o {@code 0.0} si no hay filas que promediar (la query usa COALESCE)
     */
    @Query("SELECT COALESCE(AVG(p.remuneracionPretendida), 0.0) FROM Postulacion p " +
            "WHERE p.remuneracionPretendida IS NOT NULL AND p.remuneracionPretendida > 0")
    Double promedioRemuneracionPositiva();

    /**
     * Suma de {@code remuneracionPretendida} de las postulaciones en estado
     * {@code COMPLETADA} (el estado se compara en mayúsculas): es el
     * «dinero en la comunidad» del dashboard, un total <b>pretendido</b>, no cobrado.
     *
     * @return suma o {@code 0.0} si no hay filas (COALESCE)
     */
    @Query("SELECT COALESCE(SUM(p.remuneracionPretendida), 0.0) FROM Postulacion p " +
            "WHERE UPPER(p.estado) = 'COMPLETADA' AND p.remuneracionPretendida IS NOT NULL")
    Double sumaRemuneracionCompletadas();
}