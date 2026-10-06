package com.al_lado.al_lado_project.entity.comunidad;

import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "favoritos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"empleador_id", "trabajador_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleador_id", nullable = false)
    @JsonIgnoreProperties({"favoritos", "oportunidades", "valoracionesOtorgadas", "publicidades", "password"})
    private Empleador empleador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", nullable = false)
    @JsonIgnoreProperties({"postulaciones", "especialidades", "portafolioItems", "horarios", "preferenciaNotificacion", "valoracionesRecibidas", "password"})
    private Trabajador trabajador;

    @Column(name = "fecha_guardado", nullable = false, updatable = false)
    private LocalDateTime fechaGuardado;

    @PrePersist
    protected void onCreate() {
        if (this.fechaGuardado == null) {
            this.fechaGuardado = LocalDateTime.now();
        }
    }

    public Favorito(Empleador empleador, Trabajador trabajador) {
        this.empleador = empleador;
        this.trabajador = trabajador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Favorito favorito = (Favorito) o;
        return id != null && Objects.equals(id, favorito.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}