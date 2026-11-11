package com.al_lado.al_lado_project.service.auth;

import com.al_lado.al_lado_project.dto.auth.AuthRequestDTO;
import com.al_lado.al_lado_project.dto.auth.AuthResponseDTO;
import com.al_lado.al_lado_project.dto.auth.RegisterRequestDTO;
import com.al_lado.al_lado_project.dto.auth.ChangePasswordDTO;
import com.al_lado.al_lado_project.dto.auth.ForgotPasswordRequestDTO;
import com.al_lado.al_lado_project.dto.auth.ResetPasswordRequestDTO;

/**
 * Autenticación, registro y recuperación de contraseñas.
 *
 * <p>La cuenta debe estar habilitada para ingresar. La verificación del email no es un requisito
 * de acceso. El controller comprueba que los cambios de contraseña correspondan al usuario de la
 * sesión.</p>
 */
public interface AuthService {

    /**
     * Valida email y contraseña y devuelve un JWT con los roles del usuario.
     *
     * <p>Rechaza credenciales incorrectas y cuentas deshabilitadas.</p>
     *
     * @param request email y contraseña
     * @return token, email y roles
     * @throws org.springframework.security.core.AuthenticationException
     *     si las credenciales no son válidas
     */
    AuthResponseDTO login(AuthRequestDTO request);

    /**
     * Registra un trabajador o empleador y devuelve su JWT.
     *
     * <p>El email debe ser único y la contraseña se guarda con BCrypt. Si no se indica rol, usa
     * TRABAJADOR. Este método no permite crear administradores.</p>
     *
     * @param request datos del registro
     * @return token recién emitido, email y roles
     * @throws com.al_lado.al_lado_project.exception.DuplicateResourceException
     *     si el email ya existe
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si el rol no es válido
     */
    AuthResponseDTO register(RegisterRequestDTO request);

    /**
     * Marca el email como verificado usando el JWT recibido.
     *
     * <p>Usa el mismo tipo de token que el acceso. Rechaza tokens inválidos o vencidos; no exige
     * un token exclusivo de verificación.</p>
     *
     * @param token JWT de verificación
     * @throws IllegalArgumentException
     *     si el token es inválido/expirado o el usuario no existe
     */
    void verifyEmail(String token);

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
     * Genera y guarda un token de recuperación válido por 24 horas.
     *
     * <p>Rechaza emails no registrados. Este método no envía el token por correo.</p>
     *
     * @param request email de la cuenta
     * @throws IllegalArgumentException
     *     si no existe una cuenta con ese email
     */
    void forgotPassword(ForgotPasswordRequestDTO request);

    /**
     * Restablece la contraseña con un token de recuperación vigente.
     *
     * <p>Guarda la nueva contraseña codificada y elimina el token y su vencimiento para impedir
     * que vuelva a utilizarse.</p>
     *
     * @param request token y nueva contraseña
     * @throws IllegalArgumentException
     *     si el token no existe o está vencido
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
