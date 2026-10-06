package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComunicadoRepository extends JpaRepository<Comunicado, Long> {

    @EntityGraph(attributePaths = {"emisor"})
    Page<Comunicado> findByAlcanceIgnoreCase(String alcance, Pageable pageable);

    @EntityGraph(attributePaths = {"emisor"})
    Page<Comunicado> findAllByOrderByFechaEmisionDesc(Pageable pageable);
}
