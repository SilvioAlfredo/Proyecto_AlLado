package com.al_lado.al_lado_project.dto;

public abstract class PostBaseDTO {
    private Long id;
    private String titulo;
    private String descripcion;

    private String imagen; // Ruta o URL de la imagen
    private UsuarioDTO autor;
}