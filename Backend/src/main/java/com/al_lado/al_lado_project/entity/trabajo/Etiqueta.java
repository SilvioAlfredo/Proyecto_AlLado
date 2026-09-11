package com.al_lado.al_lado_project.entity.trabajo;

import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "etiquetas")
@Getter
@Setter
@NoArgsConstructor
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    // Relación bidireccional M:N con OportunidadLaboral
    @JsonIgnore
    @ManyToMany(mappedBy = "etiquetas")
    private Set<OportunidadLaboral> oportunidades = new HashSet<>();

    // Relación bidireccional M:N con Trabajador
    @JsonIgnore
    @ManyToMany(mappedBy = "especialidades")
    private Set<Trabajador> trabajadores = new HashSet<>();

    public Etiqueta(String nombre) {
        this.nombre = nombre;
    }
}