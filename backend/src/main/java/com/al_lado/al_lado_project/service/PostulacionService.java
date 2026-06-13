package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.entity.Postulacion;
import java.util.List;

public interface PostulacionService {

    List<Postulacion> obtenerTodas();

    Postulacion obtenerPorId(Long id);

    Postulacion crearPostulacion(Postulacion postulacion);

    Postulacion seleccionarPostulacion(Long id);

    Postulacion rechazarPostulacion(Long id);

    void eliminar(Long id);
}