package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificacionDTO {

    private Long id;

    @NotNull(message = "El destinatario es obligatorio.")
    private UsuarioResumenDTO destinatario;

    @NotBlank(message = "El título es obligatorio.")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres.")
    private String titulo;

    @NotBlank(message = "El mensaje no puede estar vacío.")
    private String mensaje;

    @Pattern(regexp = "POSTULACION_RECIBIDA|POSTULACION_ESTADO|SISTEMA",
            message = "Tipo inválido. Valores permitidos: POSTULACION_RECIBIDA, POSTULACION_ESTADO, SISTEMA.")
    private String tipo;

    private Long referenciaId;

    private boolean leida;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaCreacion;
}
