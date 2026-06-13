package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.entity.Empleador;
import java.util.List;

public interface EmpleadorService {

    List<Empleador> obtenerTodos();

    Empleador obtenerPorId(Long id);

    Empleador guardar(Empleador empleador);

    void eliminar(Long id);
}