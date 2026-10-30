package com.al_lado.al_lado_project.entity.trabajo;

import com.al_lado.al_lado_project.entity.usuario.Empleador;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "oportunidades_laborales")
@PrimaryKeyJoinColumn(name = "post_id")
@Getter
@Setter
@NoArgsConstructor
public class OportunidadLaboral extends PostBase {

    @Column(length = 100)
    private String ubicacion;

    @Column(length = 50)
    private String modalidad; // Ej: "PRESENCIAL", "REMOTO", "HIBRIDO"

    @Column(name = "rango_salarial", length = 100)
    private String rangoSalarial;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "dia_semana_requerido", length = 20)
    private String diaSemanaRequerido;

    @Column(name = "hora_inicio_requerida")
    private LocalTime horaInicioRequerida;

    @Column(name = "hora_fin_requerida")
    private LocalTime horaFinRequerida;

    @Column(nullable = false)
    private boolean activa = true;

    // HU empleador "Oportunidad laboral pro": las destacadas (solo plan PRO)
    // se muestran primero en listados y propuestas. El default a nivel DDL
    // evita que ddl-auto=update falle en tablas con filas existentes.
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean destacada = false;

    // Postulaciones asociadas
    @OneToMany(mappedBy = "oportunidadLaboral")
    private Set<Postulacion> postulaciones = new HashSet<>();

    // Relación M:N con Etiqueta
    @ManyToMany
    @JoinTable(
            name = "oportunidad_etiqueta",
            joinColumns = @JoinColumn(name = "oportunidad_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    private Set<Etiqueta> etiquetas = new HashSet<>();

    public OportunidadLaboral(String titulo, String contenido, Empleador empleador, String ubicacion, String modalidad, String rangoSalarial) {
        super(titulo, contenido, empleador);
        this.ubicacion = ubicacion;
        this.modalidad = modalidad;
        this.rangoSalarial = rangoSalarial;
    }

    // Getter auxiliar para delegar en el autor heredado
    public Empleador getEmpleador() {
        if (getAutor() instanceof Empleador) {
            return (Empleador) getAutor();
        }
        return null;
    }

    // Setter auxiliar explícito
    public void setEmpleador(Empleador empleador) {
        setAutor(empleador);
    }

    // Métodos auxiliares para gestionar etiquetas (sincronizan ambos lados M:N)
    public void addEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.add(etiqueta);
        etiqueta.getOportunidades().add(this);
    }

    public void removeEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.remove(etiqueta);
        etiqueta.getOportunidades().remove(this);
    }

    // Métodos auxiliares para gestionar postulaciones
    public void addPostulacion(Postulacion postulacion) {
        postulaciones.add(postulacion);
        postulacion.setOportunidadLaboral(this);
    }
}