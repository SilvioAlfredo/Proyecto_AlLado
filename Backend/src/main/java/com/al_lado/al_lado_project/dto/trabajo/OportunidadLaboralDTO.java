package com.al_lado.al_lado_project.dto.trabajo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class OportunidadLaboralDTO extends PostBaseDTO {

    @Size(max = 100, message = "La ubicación no puede superar los 100 caracteres.")
    private String ubicacion;
    @Size(max = 50, message = "La modalidad no puede superar los 50 caracteres.")
    @Pattern(regexp = "PRESENCIAL|REMOTO|HIBRIDO", message = "Modalidad inválida. Valores permitidos: PRESENCIAL, REMOTO, HIBRIDO.")
    private String modalidad;
    @Size(max = 100, message = "El rango salarial no puede superar los 100 caracteres.")
    private String rangoSalarial;
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitud;
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitud;
    @Pattern(regexp = "LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO")
    private String diaSemanaRequerido;
    private java.time.LocalTime horaInicioRequerida;
    private java.time.LocalTime horaFinRequerida;
    private boolean activa;
    private boolean destacada;

    private Set<EtiquetaDTO> etiquetas = new HashSet<>();
    private List<PostulacionResumenDTO> postulaciones = new ArrayList<>();

    @AssertTrue(message = "Debe indicar latitud y longitud juntas.")
    public boolean isParCoordenadasValido() {
        return (latitud == null) == (longitud == null);
    }

    @AssertTrue(message = "Para indicar disponibilidad requerida, complete día, hora de inicio y hora de fin.")
    public boolean isHorarioRequeridoValido() {
        boolean sinHorario = diaSemanaRequerido == null && horaInicioRequerida == null && horaFinRequerida == null;
        boolean horarioCompleto = diaSemanaRequerido != null && horaInicioRequerida != null
                && horaFinRequerida != null && horaInicioRequerida.isBefore(horaFinRequerida);
        return sinHorario || horarioCompleto;
    }

    /**
     * Cantidad de postulaciones recibidas. Se informa en todas las lecturas para que
     * la proyección pública (AL-SEC-006) pueda mostrar el interés sin exponer el
     * detalle de los postulantes.
     */
    private Integer postulacionesCount;
}