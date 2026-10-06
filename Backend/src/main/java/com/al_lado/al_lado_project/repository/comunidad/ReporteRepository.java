package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Reporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    @EntityGraph(attributePaths = {"reportante", "reportado", "moderador"})
    Page<Reporte> findByEstadoIgnoreCase(String estado, Pageable pageable);

    List<Reporte> findByReportadoId(Long reportadoId);

    List<Reporte> findByReportanteId(Long reportanteId);

    List<Reporte> findByModeradorId(Long moderadorId);
}
