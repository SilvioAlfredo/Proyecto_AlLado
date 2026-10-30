package com.al_lado.al_lado_project.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
// estructura del json que devuelve el token al backend para mantener sesion y roles
    private String token;
    private String email;
    private List<String> roles;
}