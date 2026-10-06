package com.al_lado.al_lado_project.repository.trabajo;

import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {

    List<Postulacion> findByOportunidadLaboralId(Long oportunidadId);

    List<Postulacion> findByAutorId(Long autorId);

    Optional<Postulacion> findByOportunidadLaboralIdAndAutorId(Long oportunidadId, Long autorId);

    boolean existsByOportunidadLaboralIdAndAutorId(Long oportunidadId, Long autorId);

    // Conteos por autor y estado para las estadísticas del trabajador.
    long countByAutorId(Long autorId);

    long countByAutorIdAndEstadoIgnoreCase(Long autorId, String estado);

    // Agregaciones en BD para el dashboard: conteo, suma y promedio sin
    // cargar todas las postulaciones a memoria.
    long countByEstadoIgnoreCase(String estado);

    @Query("SELECT COALESCE(AVG(p.remuneracionPretendida), 0.0) FROM Postulacion p " +
            "WHERE p.remuneracionPretendida IS NOT NULL AND p.remuneracionPretendida > 0")
    Double promedioRemuneracionPositiva();

    @Query("SELECT COALESCE(SUM(p.remuneracionPretendida), 0.0) FROM Postulacion p " +
            "WHERE UPPER(p.estado) = 'COMPLETADA' AND p.remuneracionPretendida IS NOT NULL")
    Double sumaRemuneracionCompletadas();
}