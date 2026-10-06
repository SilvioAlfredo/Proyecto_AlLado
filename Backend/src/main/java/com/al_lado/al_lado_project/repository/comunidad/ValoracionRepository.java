package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    List<Valoracion> findByTrabajadorId(Long trabajadorId);

    List<Valoracion> findByAutorId(Long autorId);

    @EntityGraph(attributePaths = {"autor", "trabajador"})
    Page<Valoracion> findByTrabajadorId(Long trabajadorId, Pageable pageable);

    @EntityGraph(attributePaths = {"autor", "trabajador"})
    Page<Valoracion> findByAutorId(Long autorId, Pageable pageable);

    boolean existsByAutorIdAndTrabajadorId(Long autorId, Long trabajadorId);

    @Query("SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.trabajador.id = :trabajadorId")
    Double calcularPromedioPuntuacionPorTrabajadorId(@Param("trabajadorId") Long trabajadorId);

    @Query("SELECT COUNT(v) FROM Valoracion v WHERE v.trabajador.id = :trabajadorId")
    Long countByTrabajadorId(@Param("trabajadorId") Long trabajadorId);
}