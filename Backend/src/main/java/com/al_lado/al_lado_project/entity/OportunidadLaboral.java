package com.al_lado.al_lado_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "oportunidades_laborales")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OportunidadLaboral extends PostBase {

    @Column(nullable = false, length = 50)
    private String estado; // Ejemplo: ACTIVA, CANCELADA, CERRADA

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_publicacion")
    private Date fechaPublicacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_cierre")
    private Date fechaCierre;

    // Relación de tu diagrama: OportunidadLaboral "1" --> "0..*" Postulacion
    @OneToMany(mappedBy = "oportunidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Postulacion> postulacionesRecibidas = new ArrayList<>();
}