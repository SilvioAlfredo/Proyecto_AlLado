package com.al_lado.al_lado_project.service.perfil;

import com.al_lado.al_lado_project.dto.perfil.PortafolioItemDTO;

import java.util.List;

public interface PortafolioItemService {
    List<PortafolioItemDTO> obtenerPorTrabajador(Long trabajadorId);
    PortafolioItemDTO agregar(Long trabajadorId, PortafolioItemDTO dto);
    void eliminar(Long id);
}
