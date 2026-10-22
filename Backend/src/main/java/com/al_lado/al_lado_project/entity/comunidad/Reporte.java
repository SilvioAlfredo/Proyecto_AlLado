package com.al_lado.al_lado_project.entity.comunidad;

import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reportes")
@Getter
@Setter
@NoArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String motivo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(length = 100)
    private String sector;

    @Column(length = 30, nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reportante_id", nullable = false)
    @JsonIgnoreProperties({"reportesEnviados", "reportesRecibidos", "publicacionesForo", "donaciones", "suscripcion", "password"})
    private Usuario reportante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reportado_id")
    @JsonIgnoreProperties({"reportesEnviados", "reportesRecibidos", "publicacionesForo", "donaciones", "suscripcion", "password"})
    private Usuario reportado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderador_id")
    @JsonIgnoreProperties({"reportesModerados", "comunicadosEnviados", "perfilesSupervisados", "password"})
    private Administrador moderador;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }

    public Reporte(String motivo, String descripcion, String sector, Usuario reportante, Usuario reportado) {
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.sector = sector;
        this.reportante = reportante;
        this.reportado = reportado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reporte reporte = (Reporte) o;
        return id != null && Objects.equals(id, reporte.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}