package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;

import java.util.List;

public interface ComunicadoService {
    List<ComunicadoDTO> obtenerTodos();
    ComunicadoDTO obtenerPorId(Long id);
    List<ComunicadoDTO> obtenerPorAlcance(String alcance);
    ComunicadoDTO crear(ComunicadoDTO dto);
    void eliminar(Long id);
}
