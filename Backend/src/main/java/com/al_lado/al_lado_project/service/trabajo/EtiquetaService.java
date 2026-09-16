package com.al_lado.al_lado_project.service.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.EtiquetaDTO;

import java.util.List;

public interface EtiquetaService {
    List<EtiquetaDTO> obtenerTodas();
    EtiquetaDTO obtenerPorId(Long id);
    EtiquetaDTO obtenerOCrearPorNombre(String nombre);
    void eliminar(Long id);
}
