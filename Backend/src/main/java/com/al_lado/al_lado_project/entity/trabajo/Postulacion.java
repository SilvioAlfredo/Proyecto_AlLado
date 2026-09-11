package com.al_lado.al_lado_project.entity.trabajo;

import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "postulaciones")
@PrimaryKeyJoinColumn(name = "post_id")
@Getter
@Setter
@NoArgsConstructor
public class Postulacion extends PostBase {

    @Column(length = 50, nullable = false)
    private String estado = "PENDIENTE"; // Ej: "PENDIENTE", "ACEPTADA", "RECHAZADA"

    @Column(name = "remuneracion_pretendida")
    private Double remuneracionPretendida;

    // Oferta laboral a la que aplica
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidad_id", nullable = false)
    private OportunidadLaboral oportunidadLaboral;

    public Postulacion(String titulo, String contenido, Trabajador trabajador, OportunidadLaboral oportunidadLaboral, Double remuneracionPretendida) {
        super(titulo, contenido, trabajador);
        this.oportunidadLaboral = oportunidadLaboral;
        this.remuneracionPretendida = remuneracionPretendida;
    }

    // Helper getter para obtener directamente el objeto Trabajador casteado desde la clase base
    public Trabajador getTrabajador() {
        if (getAutor() instanceof Trabajador) {
            return (Trabajador) getAutor();
        }
        return null;
    }

    // Helper setter explícito
    public void setTrabajador(Trabajador trabajador) {
        setAutor(trabajador);
    }
}