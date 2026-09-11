package com.al_lado.al_lado_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "postulaciones")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Postulacion extends PostBase {

    private Double presupuesto;

    @Column(length = 100)
    private String disponibilidad;

    @Column(nullable = false, length = 50)
    private String estado; // PENDIENTE, SELECCIONADA, RECHAZADA

    // Relación de tu diagrama: OportunidadLaboral "1" --> "0..*" Postulacion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidad_id", nullable = false)
    private OportunidadLaboral oportunidad;
}