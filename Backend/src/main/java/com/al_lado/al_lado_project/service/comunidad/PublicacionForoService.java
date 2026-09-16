package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;

import java.util.List;

public interface PublicacionForoService {
    List<PublicacionForoDTO> obtenerTodas();
    PublicacionForoDTO obtenerPorId(Long id);
    List<PublicacionForoDTO> obtenerPorAutor(Long autorId);
    PublicacionForoDTO crear(PublicacionForoDTO dto);
    void eliminar(Long id);
}
