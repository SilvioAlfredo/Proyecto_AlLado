package com.al_lado.al_lado_project.service.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;

import java.util.List;

public interface PostulacionService {
    List<PostulacionDTO> obtenerTodas();
    PostulacionDTO obtenerPorId(Long id);
    PostulacionDTO crearPostulacion(PostulacionDTO postulacionDTO);
    PostulacionDTO seleccionarPostulacion(Long id);
    PostulacionDTO rechazarPostulacion(Long id);
    void eliminar(Long id);
}