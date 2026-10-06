package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionForoRepository extends JpaRepository<PublicacionForo, Long> {

    @EntityGraph(attributePaths = {"autor"})
    Page<PublicacionForo> findByAutorId(Long autorId, Pageable pageable);

    @EntityGraph(attributePaths = {"autor"})
    Page<PublicacionForo> findAllByOrderByFechaCreacionDesc(Pageable pageable);
}
