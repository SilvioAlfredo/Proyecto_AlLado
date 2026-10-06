package com.al_lado.al_lado_project.dto.trabajo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostulacionDTO extends PostBaseDTO {

    @Pattern(regexp = "PENDIENTE|SELECCIONADA|RECHAZADA|COMPLETADA", message = "Estado inválido. Valores permitidos: PENDIENTE, SELECCIONADA, RECHAZADA, COMPLETADA.")
    private String estado;
    private Double remuneracionPretendida;

    @NotNull(message = "La oportunidad laboral a la que aplica es obligatoria.")
    private OportunidadLaboralResumenDTO oportunidadLaboral;
}