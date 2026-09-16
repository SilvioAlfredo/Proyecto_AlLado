package com.al_lado.al_lado_project.service.usuario;

import com.al_lado.al_lado_project.dto.usuario.EmpleadorDTO;

import java.util.List;

public interface EmpleadorService {
    List<EmpleadorDTO> obtenerTodos();
    EmpleadorDTO obtenerPorId(Long id);
    EmpleadorDTO guardar(EmpleadorDTO empleadorDTO);
    void eliminar(Long id);
}