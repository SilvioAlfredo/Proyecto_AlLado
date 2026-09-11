package com.al_lado.al_lado_project.entity.trabajo;

import com.al_lado.al_lado_project.entity.usuario.Empleador;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
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

    @Column(nullable = false)
    private boolean activa = true;

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

    // Helper getter para delegar en el autor heredado
    public Empleador getEmpleador() {
        if (getAutor() instanceof Empleador) {
            return (Empleador) getAutor();
        }
        return null;
    }

    // Helper setter explícito
    public void setEmpleador(Empleador empleador) {
        setAutor(empleador);
    }

    // Helper methods para gestionar postulaciones
    public void addPostulacion(Postulacion postulacion) {
        postulaciones.add(postulacion);
        postulacion.setOportunidadLaboral(this);
    }

    public void removePostulacion(Postulacion postulacion) {
        postulaciones.remove(postulacion);
        postulacion.setOportunidadLaboral(null);
    }
}