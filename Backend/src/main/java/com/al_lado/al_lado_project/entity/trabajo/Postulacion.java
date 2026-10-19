package com.al_lado.al_lado_project.entity.trabajo;

import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Postulación de un trabajador a una oportunidad; subtipo de {@link PostBase} con
 * tabla propia unida por {@code @PrimaryKeyJoinColumn(name = "post_id")}.
 *
 * <p>Los estados posibles están fijados en las constantes de la clase
 * ({@link #ESTADO_PENDIENTE}, {@link #ESTADO_SELECCIONADA},
 * {@link #ESTADO_RECHAZADA}, {@link #ESTADO_COMPLETADA}, {@link #ESTADO_RETIRADA});
 * el campo nace en {@code PENDIENTE} y <b>es un string, sin validación de dominio en
 * la entidad</b>: el ciclo de vida lo hacen los servicios.</p>
 *
 * <p>{@code remuneracionPretendida} es un valor declarado, opcional (puede ser
 * {@code null} y no se valida aquí); {@code oportunidadLaboral} es {@code LAZY} y
 * obligatoria.</p>
 */
@Entity
@Table(name = "postulaciones")
@PrimaryKeyJoinColumn(name = "post_id")
@Getter
@Setter
@NoArgsConstructor
public class Postulacion extends PostBase {

    /** Estado inicial que asigna la entidad; lo sobrescribe el servicio al crear. */
    @Column(length = 50, nullable = false)
    private String estado = "PENDIENTE"; // Ej: "PENDIENTE", "SELECCIONADA", "RECHAZADA", "COMPLETADA", "RETIRADA"

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_SELECCIONADA = "SELECCIONADA";
    public static final String ESTADO_RECHAZADA = "RECHAZADA";
    public static final String ESTADO_COMPLETADA = "COMPLETADA";
    public static final String ESTADO_RETIRADA = "RETIRADA";

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

    /**
     * Getter auxiliar que intenta devolver el autor ya convertido a
     * {@link Trabajador}.
     *
     * <p><b>Puede devolver {@code null} aunque el autor sea un trabajador</b>: el
     * lado {@code autor} está declarado como {@code Usuario} y llega como proxy lazy
     * de esa clase base, por lo que {@code instanceof Trabajador} no lo reconoce
     * hasta que se inicializa. Por eso los servicios usan {@code getAutor()} (y
     * {@code getAutor().getId()}) en lugar de este método; ver
     * {@code PostulacionServiceImpl#notificarCambioEstado}.</p>
     *
     * @return el autor como trabajador, o {@code null} si no es reconocible aún
     */
    public Trabajador getTrabajador() {
        if (getAutor() instanceof Trabajador) {
            return (Trabajador) getAutor();
        }
        return null;
    }

    /** Setter auxiliar equivaliente a {@code setAutor}; sin uso en el código actual. */
    public void setTrabajador(Trabajador trabajador) {
        setAutor(trabajador);
    }
}