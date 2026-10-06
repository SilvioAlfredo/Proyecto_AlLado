package com.al_lado.al_lado_project.dto.comunidad;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonacionDTO {

    private Long id;

    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero.")
    private BigDecimal monto;

    private String moneda;

    private String mecanismoPago;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaDonacion;

    @NotNull(message = "El usuario donante es obligatorio.")
    private UsuarioResumenDTO usuario;

    @Pattern(regexp = "PENDIENTE|VERIFICADO|RECHAZADO", message = "Estado inválido. Valores permitidos: PENDIENTE, VERIFICADO, RECHAZADO.")
    private String estado; // PENDIENTE, VERIFICADO, RECHAZADO

    private String banco;

    private String tipoTransferencia;

    private String asuntoTransferencia; // Para verificación por admin
}