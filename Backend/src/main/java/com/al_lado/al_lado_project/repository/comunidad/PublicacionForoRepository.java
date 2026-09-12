package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionForoRepository extends JpaRepository<PublicacionForo, Long> {

    List<PublicacionForo> findByAutorId(Long autorId);

    List<PublicacionForo> findAllByOrderByFechaCreacionDesc();
}