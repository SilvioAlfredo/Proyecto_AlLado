package com.al_lado.al_lado_project.entity.perfil;

import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "portafolio_items")
@Getter
@Setter
@NoArgsConstructor
public class PortafolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "url_archivo", length = 255)
    private String urlArchivo; // URL de la imagen, PDF o enlace externo

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", nullable = false)
    @JsonIgnore
    private Trabajador trabajador;

    public PortafolioItem(String titulo, String descripcion, String urlArchivo, Trabajador trabajador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.urlArchivo = urlArchivo;
        this.trabajador = trabajador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortafolioItem that = (PortafolioItem) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}