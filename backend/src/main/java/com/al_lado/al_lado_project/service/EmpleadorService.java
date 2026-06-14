package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.entity.Empleador;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmpleadorService {

    List<Empleador> obtenerTodos();

    EmpleadorResponseDTO obtenerPorId(Long id);

    Empleador guardar(Empleador empleador);

    @Transactional
    EmpleadorResponseDTO registrarEmpleador(EmpleadorRequestDTO requestDTO);

    @Transactional
    EmpleadorResponseDTO actualizar(Long id, EmpleadorRequestDTO requestDTO);

    void eliminar(Long id);
}