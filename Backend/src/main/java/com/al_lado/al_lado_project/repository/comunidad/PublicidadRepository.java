package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Publicidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PublicidadRepository extends JpaRepository<Publicidad, Long> {

    List<Publicidad> findByAnuncianteId(Long anuncianteId);

    List<Publicidad> findByActivaTrue();

    @Query("SELECT p FROM Publicidad p WHERE p.activa = true AND :fechaActual BETWEEN p.fechaInicio AND p.fechaFin")
    List<Publicidad> buscarPublicidadesVigentes(@Param("fechaActual") LocalDate fechaActual);
}