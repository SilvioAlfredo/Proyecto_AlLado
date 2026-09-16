package com.al_lado.al_lado_project.service.usuario;

import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;

import java.util.List;

public interface SuscripcionService {
    List<SuscripcionDTO> obtenerTodas();
    SuscripcionDTO obtenerPorId(Long id);
    SuscripcionDTO obtenerPorUsuario(Long usuarioId);
    SuscripcionDTO crear(SuscripcionDTO dto);
    SuscripcionDTO cancelar(Long id);
    void eliminar(Long id);
}
