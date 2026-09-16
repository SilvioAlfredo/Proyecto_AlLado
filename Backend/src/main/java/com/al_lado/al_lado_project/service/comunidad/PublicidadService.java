package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.PublicidadDTO;

import java.util.List;

public interface PublicidadService {
    List<PublicidadDTO> obtenerTodas();
    PublicidadDTO obtenerPorId(Long id);
    List<PublicidadDTO> obtenerPorAnunciante(Long anuncianteId);
    List<PublicidadDTO> obtenerVigentes();
    PublicidadDTO guardar(PublicidadDTO dto);
    void eliminar(Long id);
}
