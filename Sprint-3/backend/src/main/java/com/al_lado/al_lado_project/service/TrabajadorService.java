package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.entity.Trabajador;
import java.util.List;

public interface TrabajadorService {

    List<Trabajador> obtenerTodos();

    Trabajador obtenerPorId(Long id);

    Trabajador guardar(Trabajador trabajador);

    void eliminar(Long id);
}
