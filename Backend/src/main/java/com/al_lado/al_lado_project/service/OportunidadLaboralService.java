package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.OportunidadLaboralDTO;
import java.util.List;

public interface OportunidadLaboralService {
    List<OportunidadLaboralDTO> obtenerTodas();
    OportunidadLaboralDTO obtenerPorId(Long id);
    OportunidadLaboralDTO guardar(OportunidadLaboralDTO oportunidadDTO);
    OportunidadLaboralDTO cerrarOportunidad(Long id);
    void eliminar(Long id);
}