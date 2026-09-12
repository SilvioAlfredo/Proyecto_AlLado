package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    List<Valoracion> findByTrabajadorId(Long trabajadorId);

    List<Valoracion> findByAutorId(Long autorId);

    boolean existsByAutorIdAndTrabajadorId(Long autorId, Long trabajadorId);

    @Query("SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.trabajador.id = :trabajadorId")
    Double calcularPromedioPuntuacionPorTrabajadorId(@Param("trabajadorId") Long trabajadorId);
}