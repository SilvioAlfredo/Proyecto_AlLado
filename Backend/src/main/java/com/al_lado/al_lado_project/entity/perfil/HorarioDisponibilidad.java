package com.al_lado.al_lado_project.entity.perfil;

import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "horarios_disponibilidad")
@Getter
@Setter
@NoArgsConstructor
public class HorarioDisponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dia_semana", nullable = false, length = 20)
    private String diaSemana; // Ej: "LUNES", "MARTES", "SABADO"

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", nullable = false)
    @JsonIgnore
    private Trabajador trabajador;

    public HorarioDisponibilidad(String diaSemana, LocalTime horaInicio, LocalTime horaFin, Trabajador trabajador) {
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.trabajador = trabajador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HorarioDisponibilidad that = (HorarioDisponibilidad) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}