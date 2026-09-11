package com.al_lado.al_lado_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trabajadores")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Trabajador extends Usuario {

    // Relación de tu diagrama: Trabajador "1" --> "0..*" Postulacion
    // Nota: Como Postulacion hereda de PostBase, su campo "autor" (heredado) apuntará a este Trabajador
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Postulacion> postulaciones = new ArrayList<>();
}