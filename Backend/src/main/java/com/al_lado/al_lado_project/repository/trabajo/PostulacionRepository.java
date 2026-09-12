package com.al_lado.al_lado_project.repository.trabajo;

import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {

    List<Postulacion> findByOportunidadLaboralId(Long oportunidadId);

    List<Postulacion> findByAutorId(Long autorId);

    Optional<Postulacion> findByOportunidadLaboralIdAndAutorId(Long oportunidadId, Long autorId);

    boolean existsByOportunidadLaboralIdAndAutorId(Long oportunidadId, Long autorId);
}