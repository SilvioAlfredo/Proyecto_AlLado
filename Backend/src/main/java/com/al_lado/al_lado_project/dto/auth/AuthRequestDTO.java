package com.al_lado.al_lado_project.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {
// se blinda la entrada con datos incorrectos
    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "Debe proporcionar un formato de email válido.")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String password;
}