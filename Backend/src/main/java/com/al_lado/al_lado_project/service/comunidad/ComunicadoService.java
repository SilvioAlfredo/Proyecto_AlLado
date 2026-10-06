package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComunicadoService {
    Page<ComunicadoDTO> obtenerTodos(Pageable pageable);
    ComunicadoDTO obtenerPorId(Long id);
    Page<ComunicadoDTO> obtenerPorAlcance(String alcance, Pageable pageable);
    ComunicadoDTO crear(ComunicadoDTO dto);
    void eliminar(Long id);
}
