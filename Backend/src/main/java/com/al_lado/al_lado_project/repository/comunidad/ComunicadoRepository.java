package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

@Repository
public interface ComunicadoRepository extends JpaRepository<Comunicado, Long>, JpaSpecificationExecutor<Comunicado> {

    @EntityGraph(attributePaths = {"emisor"})
    Page<Comunicado> findAll(Specification<Comunicado> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"emisor"})
    Page<Comunicado> findByAlcanceIgnoreCase(String alcance, Pageable pageable);

    @EntityGraph(attributePaths = {"emisor"})
    Page<Comunicado> findAllByOrderByFechaEmisionDesc(Pageable pageable);
}
