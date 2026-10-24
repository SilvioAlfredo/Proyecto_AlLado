package com.al_lado.al_lado_project.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Respuesta de los flujos de autenticación (login y registro).
 *
 * <p><b>Identidad mínima (AL-API-005):</b> además del token, la respuesta incluye el
 * identificador ({@code id}) y el nombre del usuario. Con eso el cliente arma la
 * sesión sin necesidad de listar usuarios para descubrir el propio, que era lo que
 * filtraba datos de terceros en las lecturas públicas.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;

    /** Identificador del usuario autenticado. */
    private Long id;

    private String email;
    private String nombre;
    private List<String> roles;

    /**
     * Token de verificación de correo, solo en el registro.
     *
     * <p>Mientras no haya proveedor de correo (AL-SEC-004), el token se devuelve aquí para
     * que el flujo de verificación sea utilizable. Con el proveedor habilitado se envía por
     * email y la respuesta lo omite.</p>
     */
    private String verificacionEmailToken;
}
