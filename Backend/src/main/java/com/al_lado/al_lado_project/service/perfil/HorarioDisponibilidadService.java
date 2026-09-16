package com.al_lado.al_lado_project.service.perfil;

import com.al_lado.al_lado_project.dto.perfil.HorarioDisponibilidadDTO;

import java.util.List;

public interface HorarioDisponibilidadService {
    List<HorarioDisponibilidadDTO> obtenerPorTrabajador(Long trabajadorId);
    HorarioDisponibilidadDTO agregar(Long trabajadorId, HorarioDisponibilidadDTO dto);
    void eliminar(Long id);
}
