package com.al_lado.al_lado_project.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDTO {

    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "Debe proporcionar un formato de email válido.")
    private String email;
}