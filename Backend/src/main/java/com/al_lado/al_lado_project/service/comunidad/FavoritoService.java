package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.FavoritoDTO;

import java.util.List;

public interface FavoritoService {
    List<FavoritoDTO> obtenerPorEmpleador(Long empleadorId);
    FavoritoDTO agregar(Long empleadorId, Long trabajadorId);
    void quitar(Long empleadorId, Long trabajadorId);
}
