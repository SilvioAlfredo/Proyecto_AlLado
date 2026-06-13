package com.al_lado.al_lado_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts_base")
@Inheritance(strategy = InheritanceType.JOINED) // Tablas separadas para Oportunidad y Postulación
@Data
@NoArgsConstructor
public abstract class PostBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String imagen; // Ruta o URL de la imagen

    // Relación implícita: Toda publicación tiene un Usuario creador (Autor)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario autor;
}