package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;

import java.util.List;

public interface ValoracionService {
    List<ValoracionDTO> obtenerPorTrabajador(Long trabajadorId);
    List<ValoracionDTO> obtenerPorAutor(Long autorId);
    Double obtenerPromedioPorTrabajador(Long trabajadorId);
    ValoracionDTO crear(ValoracionDTO dto);
    void eliminar(Long id);
}
