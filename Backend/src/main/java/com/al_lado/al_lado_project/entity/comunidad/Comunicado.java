package com.al_lado.al_lado_project.entity.comunidad;

import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comunicados")
@Getter
@Setter
@NoArgsConstructor
public class Comunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @Column(length = 50)
    private String alcance; // Ej: "TODOS", "TRABAJADORES", "EMPLEADORES"

    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    // Administrador que emite el comunicado (coincide con mappedBy = "emisor" en Administrador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrador_id", nullable = false)
    @JsonIgnoreProperties({"comunicadosEnviados", "reportesModerados", "perfilesSupervisados", "password"})
    private Administrador emisor;

    @PrePersist
    protected void onCreate() {
        this.fechaEmision = LocalDateTime.now();
    }

    public Comunicado(String titulo, String contenido, String alcance, Administrador emisor) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.alcance = alcance;
        this.emisor = emisor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comunicado that = (Comunicado) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}