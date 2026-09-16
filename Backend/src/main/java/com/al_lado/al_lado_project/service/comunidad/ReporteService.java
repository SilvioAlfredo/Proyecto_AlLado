package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.ReporteDTO;

import java.util.List;

public interface ReporteService {
    List<ReporteDTO> obtenerTodos();
    ReporteDTO obtenerPorId(Long id);
    List<ReporteDTO> obtenerPorEstado(String estado);
    ReporteDTO crear(ReporteDTO dto);
    ReporteDTO resolver(Long id, Long moderadorId, String nuevoEstado);
}
