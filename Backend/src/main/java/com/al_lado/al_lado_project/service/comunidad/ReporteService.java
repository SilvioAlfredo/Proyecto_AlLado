package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.ReporteDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReporteService {
    Page<ReporteDTO> obtenerTodos(Pageable pageable);
    ReporteDTO obtenerPorId(Long id);
    Page<ReporteDTO> obtenerPorEstado(String estado, Pageable pageable);
    ReporteDTO crear(ReporteDTO dto);
    ReporteDTO resolver(Long id, Long moderadorId, String nuevoEstado);
}
