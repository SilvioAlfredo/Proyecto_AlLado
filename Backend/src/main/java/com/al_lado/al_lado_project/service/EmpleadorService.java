package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.EmpleadorDTO;
import java.util.List;

public interface EmpleadorService {
    List<EmpleadorDTO> obtenerTodos();
    EmpleadorDTO obtenerPorId(Long id);
    EmpleadorDTO guardar(EmpleadorDTO empleadorDTO);
    void eliminar(Long id);
}