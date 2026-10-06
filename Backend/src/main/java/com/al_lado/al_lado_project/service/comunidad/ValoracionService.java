package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ValoracionService {
    Page<ValoracionDTO> obtenerPorTrabajador(Long trabajadorId, Pageable pageable);
    Page<ValoracionDTO> obtenerPorAutor(Long autorId, Pageable pageable);
    Double obtenerPromedioPorTrabajador(Long trabajadorId);
    ValoracionDTO crear(ValoracionDTO dto);
    void eliminar(Long id);
}
