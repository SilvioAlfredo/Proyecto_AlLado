package com.al_lado.al_lado_project.repository.usuario;

import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {

    Optional<Trabajador> findByEmail(String email);

    // Reemplaza el filtrado in-memory (findAll + streams): delega al motor SQL
    // y precarga la preferencia para no generar N+1 al notificar.
    @Query("SELECT DISTINCT t FROM Trabajador t JOIN t.especialidades e " +
            "LEFT JOIN FETCH t.preferenciaNotificacion " +
            "WHERE LOWER(e.nombre) = LOWER(:nombre)")
    List<Trabajador> findByEspecialidadNombreIgnoreCase(@Param("nombre") String nombre);
}