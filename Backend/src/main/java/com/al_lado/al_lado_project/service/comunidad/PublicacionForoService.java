package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PublicacionForoService {
    Page<PublicacionForoDTO> obtenerTodas(Pageable pageable);
    PublicacionForoDTO obtenerPorId(Long id);
    Page<PublicacionForoDTO> obtenerPorAutor(Long autorId, Pageable pageable);
    PublicacionForoDTO crear(PublicacionForoDTO dto);
    void eliminar(Long id);
}
