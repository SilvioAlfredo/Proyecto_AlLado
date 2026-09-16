package com.al_lado.al_lado_project.repository.perfil;

import com.al_lado.al_lado_project.entity.perfil.HorarioDisponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HorarioDisponibilidadRepository extends JpaRepository<HorarioDisponibilidad, Long> {

    List<HorarioDisponibilidad> findByTrabajadorId(Long trabajadorId);

    List<HorarioDisponibilidad> findByTrabajadorIdAndDiaSemanaIgnoreCase(Long trabajadorId, String diaSemana);

    @Modifying
    @Transactional
    void deleteByTrabajadorId(Long trabajadorId);
}