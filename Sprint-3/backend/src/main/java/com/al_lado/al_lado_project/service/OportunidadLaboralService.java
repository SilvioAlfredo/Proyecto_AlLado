package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.entity.OportunidadLaboral;
import java.util.List;

public interface OportunidadLaboralService {

    List<OportunidadLaboral> obtenerTodas();

    OportunidadLaboral obtenerPorId(Long id);

    OportunidadLaboral guardar(OportunidadLaboral oportunidad);

    OportunidadLaboral cerrarOportunidad(Long id);

    void eliminar(Long id);
}