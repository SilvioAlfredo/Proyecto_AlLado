package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.TrabajadorDTO;
import java.util.List;

public interface TrabajadorService {
    List<TrabajadorDTO> obtenerTodos();
    TrabajadorDTO obtenerPorId(Long id);
    TrabajadorDTO guardar(TrabajadorDTO trabajadorDTO);
    void eliminar(Long id);
}