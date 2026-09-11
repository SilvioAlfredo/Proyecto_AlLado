package com.al_lado.al_lado_project.entity.comunidad;

import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "publicidades")
@Getter
@Setter
@NoArgsConstructor
public class Publicidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "url_imagen", length = 255)
    private String urlImagen;

    @Column(name = "url_destino", length = 255)
    private String urlDestino;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private boolean activa = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anunciante_id", nullable = false)
    @JsonIgnoreProperties({"publicidades", "oportunidades", "favoritos", "valoracionesOtorgadas", "password"})
    private Empleador anunciante;

    public Publicidad(String titulo, String contenido, String urlImagen, String urlDestino, LocalDate fechaInicio, LocalDate fechaFin, Empleador anunciante) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.urlImagen = urlImagen;
        this.urlDestino = urlDestino;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.anunciante = anunciante;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Publicidad that = (Publicidad) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}