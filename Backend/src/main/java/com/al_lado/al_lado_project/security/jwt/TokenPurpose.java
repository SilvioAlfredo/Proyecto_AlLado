package com.al_lado.al_lado_project.security.jwt;

/**
 * Finalidad de un token emitido por el sistema.
 *
 * <p>Cada token lleva su propósito en el claim {@code purpose}. Un token solo se
 * acepta en el flujo que corresponde a su propósito: un token de sesión no verifica
 * un email y un token de recuperación no abre sesión. Esto impide reutilizar un
 * token en un flujo para el que no fue emitido (AL-SEC-004).</p>
 */
public enum TokenPurpose {

    /** Token de sesión: el que devuelve el login y el registro. */
    SESSION,

    /** Token de recuperación de contraseña (uso único). */
    RECOVERY,

    /** Token de verificación de correo (uso único). */
    VERIFY_EMAIL
}
