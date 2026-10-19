package com.al_lado.al_lado_project.service.usuario;

import com.al_lado.al_lado_project.dto.usuario.TrabajadorDTO;

import java.util.List;

public interface TrabajadorService {
    List<TrabajadorDTO> obtenerTodos();
    TrabajadorDTO obtenerPorId(Long id);
    TrabajadorDTO guardar(TrabajadorDTO trabajadorDTO);
    void eliminar(Long id);

    // HU empleador "Filtrar trabajadores": por especialidad (etiqueta) y/o sector barrial.
    List<TrabajadorDTO> filtrar(String etiqueta, String sector);
}