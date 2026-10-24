package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

@Repository
public interface PublicacionForoRepository extends JpaRepository<PublicacionForo, Long>, JpaSpecificationExecutor<PublicacionForo> {

    @EntityGraph(attributePaths = {"autor"})
    Page<PublicacionForo> findAll(Specification<PublicacionForo> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"autor"})
    Page<PublicacionForo> findByAutorId(Long autorId, Pageable pageable);

    @EntityGraph(attributePaths = {"autor"})
    Page<PublicacionForo> findAllByOrderByFechaCreacionDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"autor"})
    Page<PublicacionForo> findBySectorIgnoreCase(String sector, Pageable pageable);
}