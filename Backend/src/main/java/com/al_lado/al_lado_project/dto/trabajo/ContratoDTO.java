package com.al_lado.al_lado_project.dto.trabajo;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de un registro del historial de contratos.
 *
 * <p>Un "contrato" se materializa como una {@link com.al_lado.al_lado_project.entity.trabajo.Postulacion}
 * que ha alcanzado un estado de ejecución ({@code SELECCIONADA}, {@code COMPLETADA},
 * {@code RETIRADA} o {@code RECHAZADA}). Este DTO agrupa los datos relevantes para
 * mostrar el historial tanto al empleador (contratos que ha cerrado) como al
 * trabajador (contratos en los que ha participado).
 */
@Data
public class ContratoDTO {

    private Long id;
    private String estado;
    private Double remuneracionPretendida;
    private LocalDateTime fechaCreacion;

    private UsuarioResumenDTO trabajador;
    private UsuarioResumenDTO empleador;

    private OportunidadLaboralResumenDTO oportunidad;
}
