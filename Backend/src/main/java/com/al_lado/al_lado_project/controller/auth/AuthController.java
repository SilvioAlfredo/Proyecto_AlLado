package com.al_lado.al_lado_project.controller.auth;

import com.al_lado.al_lado_project.dto.auth.*;
import com.al_lado.al_lado_project.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")   // edpoint login
    // le manda un json y lo decifra y con valid ejecuta
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Identidad de la sesión autenticada (AL-API-005).
     *
     * <p>Devuelve identificador, email, nombre y roles del usuario del token. Es la fuente de
     * verdad para que el cliente rehidrate su sesión sin listar usuarios.</p>
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<IdentidadDTO> me(Authentication authentication) {
        return ResponseEntity.ok(authService.obtenerIdentidad(authentication.getName()));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    /**
     * Reemite el token de verificación de correo de la cuenta autenticada (AL-SEC-004).
     *
     * <p>El token anterior queda invalidado. Mientras no exista proveedor de correo, el token
     * se devuelve en la respuesta.</p>
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/verificacion-email")
    public ResponseEntity<Map<String, String>> solicitarVerificacionEmail(Authentication authentication) {
        String token = authService.solicitarVerificacionEmail(authentication.getName());
        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Se generó un token de verificación de correo.");
        respuesta.put("verificacionEmailToken", token);
        return ResponseEntity.ok(respuesta);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO request, Authentication authentication) {
        if (!authentication.getName().equalsIgnoreCase(request.getEmail())) {
            throw new AccessDeniedException("No puede cambiar la contraseña de otro usuario.");
        }
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/password/verify")
    public ResponseEntity<Void> verifyCurrentPassword(@Valid @RequestBody ChangePasswordDTO request, Authentication authentication) {
        if (!authentication.getName().equalsIgnoreCase(request.getEmail())) {
            throw new AccessDeniedException("No puede verificar la contraseña de otro usuario.");
        }
        authService.verifyCurrentPassword(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Solicita el envío de un enlace de recuperación (AL-SEC-005).
     *
     * <p>La respuesta es la misma exista o no la cuenta: no permite enumerar los emails
     * registrados.</p>
     */
    @PostMapping("/password/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        authService.forgotPassword(request);
        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", AuthService.MENSAJE_RECUPERACION_GENERICO);
        return ResponseEntity.ok(respuesta);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
        authService.logout(bearerToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
