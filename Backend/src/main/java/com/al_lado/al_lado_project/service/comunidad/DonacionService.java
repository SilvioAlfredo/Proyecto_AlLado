package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;

import java.util.List;

public interface DonacionService {
    List<DonacionDTO> obtenerTodas();
    DonacionDTO obtenerPorId(Long id);
    List<DonacionDTO> obtenerPorUsuario(Long usuarioId);
    DonacionDTO crear(DonacionDTO dto);
}
