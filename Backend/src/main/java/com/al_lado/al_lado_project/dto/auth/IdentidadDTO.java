package com.al_lado.al_lado_project.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Identidad de la sesión autenticada (AL-API-005).
 *
 * <p>Es la fuente de verdad de "quién soy" para el cliente: identificador, email,
 * nombre y roles. Se expone en {@code GET /auth/me} para que un cliente que
 * rehidrata la sesión (o que no guardó el id) recupere la identidad sin listar
 * usuarios.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentidadDTO {
    private Long id;
    private String email;
    private String nombre;
    private List<String> roles;
}
