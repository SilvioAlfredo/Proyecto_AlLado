package com.al_lado.al_lado_project.service.usuario;

import com.al_lado.al_lado_project.dto.usuario.UsuarioDTO;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDTO> obtenerTodos();
    UsuarioDTO obtenerPorId(Long id);
    UsuarioDTO guardar(UsuarioDTO usuarioDTO);
    void eliminar(Long id);
}