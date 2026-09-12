package com.al_lado.al_lado_project.repository.trabajo;

import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OportunidadLaboralRepository extends JpaRepository<OportunidadLaboral, Long> {

    List<OportunidadLaboral> findByActivaTrue();

    List<OportunidadLaboral> findByAutorId(Long autorId);

    List<OportunidadLaboral> findByAutorIdAndActiva(Long autorId, boolean activa);

    @Query("SELECT o FROM OportunidadLaboral o WHERE " +
            "(LOWER(o.titulo) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(o.contenido) LIKE LOWER(CONCAT('%', :filtro, '%'))) " +
            "AND o.activa = true")
    List<OportunidadLaboral> buscarActivasPorTermino(@Param("filtro") String filtro);
}