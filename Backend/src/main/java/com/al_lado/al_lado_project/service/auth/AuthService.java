package com.al_lado.al_lado_project.service.auth;

import com.al_lado.al_lado_project.dto.auth.AuthRequestDTO;
import com.al_lado.al_lado_project.dto.auth.AuthResponseDTO;
import com.al_lado.al_lado_project.dto.auth.RegisterRequestDTO;
import com.al_lado.al_lado_project.dto.auth.ChangePasswordDTO;
import com.al_lado.al_lado_project.dto.auth.ForgotPasswordRequestDTO;
import com.al_lado.al_lado_project.dto.auth.IdentidadDTO;
import com.al_lado.al_lado_project.dto.auth.ResetPasswordRequestDTO;

/**
 * Autenticación, registro y recuperación de contraseñas.
 *
 * <p>La cuenta debe estar habilitada para ingresar. La verificación del email no es un requisito
 * de acceso. El controller comprueba que los cambios de contraseña correspondan al usuario de la
 * sesión.</p>
 *
 * <p><b>Tokens con propósito (AL-SEC-004):</b> la recuperación y la verificación de correo usan
 * tokens propios, con expiración y de un solo uso. Un token de sesión no sirve para verificar un
 * email ni para recuperar una contraseña.</p>
 *
 * <p><b>Sin enumeración de usuarios (AL-SEC-005):</b> el flujo de recuperación responde siempre
 * lo mismo, exista o no la cuenta.</p>
 */
public interface AuthService {

    /**
     * Respuesta pública del flujo de recuperación (AL-SEC-005).
     *
     * <p>Es la misma exista o no la cuenta: no permite descubrir qué emails están
     * registrados.</p>
     */
    String MENSAJE_RECUPERACION_GENERICO =
            "Si la cuenta está registrada, te enviaremos las instrucciones para restablecer la contraseña.";

    /** Mensaje único para los tokens rechazados: no filtra el motivo real (AL-SEC-004). */
    String MENSAJE_TOKEN_INVALIDO = "Token inválido o expirado.";

    /**
     * Valida email y contraseña y devuelve un JWT con los roles del usuario.
     *
     * <p>Rechaza credenciales incorrectas y cuentas deshabilitadas. La respuesta incluye la
     * identidad mínima del usuario (id, email, nombre y roles).</p>
     *
     * @param request email y contraseña
     * @return token, identidad y roles
     * @throws org.springframework.security.core.AuthenticationException
     *     si las credenciales no son válidas
     */
    AuthResponseDTO login(AuthRequestDTO request);

    /**
     * Registra un trabajador o empleador y devuelve su JWT.
     *
     * <p>El email debe ser único y la contraseña se guarda con BCrypt. Si no se indica rol, usa
     * TRABAJADOR. Este método no permite crear administradores. La cuenta queda sin verificar y
     * con un token de verificación de correo de un solo uso (AL-SEC-004).</p>
     *
     * @param request datos del registro
     * @return token recién emitido, identidad, roles y token de verificación de correo
     * @throws com.al_lado.al_lado_project.exception.DuplicateResourceException
     *     si el email ya existe
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si el rol no es válido
     */
    AuthResponseDTO register(RegisterRequestDTO request);

    /**
     * Marca el email como verificado usando el token de verificación recibido.
     *
     * <p>El token debe ser de tipo {@code VERIFY_EMAIL}, no estar vencido y no haber sido
     * consumido antes (AL-SEC-004).</p>
     *
     * @param token token de verificación de correo
     * @throws IllegalArgumentException
     *     si el token es inválido, de otro propósito, está vencido o ya se usó
     */
    void verifyEmail(String token);

    /**
     * Emite un token de verificación de correo nuevo para la cuenta indicada.
     *
     * <p>El token anterior queda invalidado: solo sirve el último emitido. Mientras no haya
     * proveedor de correo, el token se devuelve al llamador.</p>
     *
     * @param email email de la cuenta
     * @return el token de verificación emitido
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe una cuenta con ese email
     */
    String solicitarVerificacionEmail(String email);

    /**
     * Devuelve la identidad de la cuenta indicada.
     *
     * @param email email de la cuenta
     * @return identificador, email, nombre y roles
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe una cuenta con ese email
     */
    IdentidadDTO obtenerIdentidad(String email);

    /**
     * Cambia la contraseña después de comprobar la actual.
     *
     * <p>Guarda la nueva contraseña codificada. El controller debe comprobar que el email
     * corresponda al usuario de la sesión.</p>
     *
     * @param request email, contraseña actual y nueva contraseña
     * @throws IllegalArgumentException
     *     si el usuario no existe o la contraseña actual no coincide
     */
    void changePassword(ChangePasswordDTO request);

    /**
     * Comprueba la contraseña actual sin modificarla.
     *
     * @param request email y contraseña actual
     * @throws IllegalArgumentException
     *     si el usuario no existe o la contraseña actual no coincide
     */
    void verifyCurrentPassword(ChangePasswordDTO request);

    /**
     * Genera y guarda un token de recuperación de un solo uso.
     *
     * <p>La vigencia la define {@code auth.token.recovery.ttl-minutes} (30 minutos por
     * defecto). El token queda invalidado al usarlo o al pedir uno nuevo.</p>
     *
     * <p><b>No revela si la cuenta existe (AL-SEC-005):</b> ante un email no registrado no
     * lanza ninguna excepción, la respuesta pública es la misma en ambos casos.</p>
     *
     * @param request email de la cuenta
     */
    void forgotPassword(ForgotPasswordRequestDTO request);

    /**
     * Restablece la contraseña con un token de recuperación vigente.
     *
     * <p>El token debe ser de tipo {@code RECOVERY}, no estar vencido y no haber sido usado
     * antes (AL-SEC-004). Al consumirse se elimina, junto con su vencimiento, para impedir que
     * vuelva a utilizarse.</p>
     *
     * @param request token y nueva contraseña
     * @throws IllegalArgumentException
     *     si el token no existe, es de otro propósito, está vencido o ya se usó
     */
    void resetPassword(ResetPasswordRequestDTO request);

    /**
     * Invalida el JWT recibido, sin cerrar las otras sesiones de la cuenta.
     *
     * <p>Guarda su identificador {@code jti} y elimina los registros de revocación vencidos. Los
     * tokens antiguos sin {@code jti} no pueden invalidarse de esta forma y se rechazan.</p>
     *
     * @param bearerToken cabecera Authorization completa
     * @throws IllegalArgumentException
     *     si falta el prefijo Bearer, el token es inválido/expirado o no tiene jti
     */
    void logout(String bearerToken);
}
