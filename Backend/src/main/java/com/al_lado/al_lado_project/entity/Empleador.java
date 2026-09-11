package com.al_lado.al_lado_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empleadores")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Empleador extends Usuario {

    // Relación de tu diagrama: Empleador "1" --> "0..*" OportunidadLaboral
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OportunidadLaboral> oportunidades = new ArrayList<>();
}