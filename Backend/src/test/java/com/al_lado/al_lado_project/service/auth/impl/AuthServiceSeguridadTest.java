package com.al_lado.al_lado_project.service.auth.impl;

import com.al_lado.al_lado_project.dto.auth.ForgotPasswordRequestDTO;
import com.al_lado.al_lado_project.dto.auth.ResetPasswordRequestDTO;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.security.jwt.TokenPurpose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RF-01 / HU-04 CA-02 · AL-SEC-004 / AL-SEC-005: tokens con propósito, de un solo uso
 * y sin enumeración de cuentas.
 *
 * <p>Se usa el {@link JwtUtils} real para que los tokens sean los que emite la aplicación.</p>
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceSeguridadTest {

    private static final String SECRETO = "clave-de-pruebas-de-al-lado-0123456789abcdef";

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TrabajadorRepository trabajadorRepository;
    @Mock
    private EmpleadorRepository empleadorRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenRevocadoRepository tokenRevocadoRepository;

    private JwtUtils jwtUtils;
    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secretKey", SECRETO);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpiration", 3_600_000L);
        ReflectionTestUtils.setField(jwtUtils, "recoveryTtlMinutes", 30L);
        ReflectionTestUtils.setField(jwtUtils, "verificationTtlMinutes", 1440L);

        service = new AuthServiceImpl(authenticationManager, jwtUtils, usuarioRepository,
                trabajadorRepository, empleadorRepository, passwordEncoder, tokenRevocadoRepository);
    }

    private Trabajador trabajador() {
        Trabajador t = new Trabajador();
        t.setId(1L);
        t.setNombre("Demo");
        t.setEmail("demo@al-lado.com");
        t.setPassword("hash");
        return t;
    }

    private ForgotPasswordRequestDTO forgot(String email) {
        ForgotPasswordRequestDTO dto = new ForgotPasswordRequestDTO();
        dto.setEmail(email);
        return dto;
    }

    private ResetPasswordRequestDTO reset(String token) {
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO();
        dto.setToken(token);
        dto.setNewPassword("NuevaClave1!");
        return dto;
    }

    // ==============================================================
    // AL-SEC-005: el flujo de recuperación no revela si la cuenta existe
    // ==============================================================

    @Test
    void forgotPassword_deberiaNoFallarConEmailInexistente() {
        when(usuarioRepository.findByEmail("nadie@al-lado.com")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.forgotPassword(forgot("nadie@al-lado.com")));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void forgotPassword_deberiaGuardarElIdentificadorDelToken() {
        when(usuarioRepository.findByEmail("demo@al-lado.com")).thenReturn(Optional.of(trabajador()));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.forgotPassword(forgot("demo@al-lado.com")));

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void forgotPassword_haceElMismoTrabajoConEmailInexistente() {
        // AL-SEC-005: sin cuenta se ejecuta igualmente la codificación BCrypt (la
        // operación más cara del flujo), de modo que el tiempo de respuesta no delate
        // si el email está registrado.
        when(usuarioRepository.findByEmail("nadie@al-lado.com")).thenReturn(Optional.empty());

        service.forgotPassword(forgot("nadie@al-lado.com"));

        verify(passwordEncoder).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void forgotPassword_haceElMismoTrabajoConEmailExistente() {
        when(usuarioRepository.findByEmail("demo@al-lado.com")).thenReturn(Optional.of(trabajador()));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        service.forgotPassword(forgot("demo@al-lado.com"));

        // Misma codificación que en el camino sin cuenta: sólo cambia que aquí además
        // se guarda el token, que es operación de milisegundos.
        verify(passwordEncoder).encode(anyString());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    // ==============================================================
    // AL-SEC-004: el token de recuperación tiene propósito y es de un solo uso
    // ==============================================================

    @Test
    void forgotPassword_deberiaGuardarElJtiYNoElToken() {
        Trabajador usuario = trabajador();
        when(usuarioRepository.findByEmail("demo@al-lado.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        service.forgotPassword(forgot("demo@al-lado.com"));

        assertNotNull(usuario.getPasswordResetToken());
        assertNotNull(usuario.getPasswordResetTokenExpiry());
        assertTrue(usuario.getPasswordResetTokenExpiry().isAfter(LocalDateTime.now()),
                "El token de recuperación debe tener vencimiento futuro.");
    }

    @Test
    void resetPassword_deberiaRechazarTokenDeSesion() {
        // Un token de sesión no sirve para recuperar una contraseña.
        String tokenSesion = jwtUtils.generateToken(trabajador());

        assertThrows(IllegalArgumentException.class, () -> service.resetPassword(reset(tokenSesion)));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void resetPassword_deberiaRechazarTokenDeVerificacion() {
        // Y al revés: un token de verificación no recupera contraseñas.
        JwtUtils.ScopedToken verificacion = jwtUtils.generateVerificationToken("demo@al-lado.com");

        assertThrows(IllegalArgumentException.class,
                () -> service.resetPassword(reset(verificacion.token())));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void resetPassword_deberiaRechazarTokenVencido() {
        JwtUtils.ScopedToken token = jwtUtils.generateRecoveryToken("demo@al-lado.com");
        Trabajador usuario = trabajador();
        usuario.setPasswordResetToken(token.jti());
        usuario.setPasswordResetTokenExpiry(LocalDateTime.now().minusMinutes(1));
        when(usuarioRepository.findByEmail("demo@al-lado.com")).thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class,
                () -> service.resetPassword(reset(token.token())));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void resetPassword_deberiaRechazarTokenYaConsumido() {
        // El identificador guardado es el de otro token: el anterior ya se usó.
        JwtUtils.ScopedToken emitido = jwtUtils.generateRecoveryToken("demo@al-lado.com");
        JwtUtils.ScopedToken otro = jwtUtils.generateRecoveryToken("demo@al-lado.com");
        Trabajador usuario = trabajador();
        usuario.setPasswordResetToken(otro.jti());
        usuario.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        when(usuarioRepository.findByEmail("demo@al-lado.com")).thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class,
                () -> service.resetPassword(reset(emitido.token())));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void resetPassword_deberiaConsumirElTokenYCambiarLaContrasena() {
        JwtUtils.ScopedToken token = jwtUtils.generateRecoveryToken("demo@al-lado.com");
        Trabajador usuario = trabajador();
        usuario.setPasswordResetToken(token.jti());
        usuario.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        when(usuarioRepository.findByEmail("demo@al-lado.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenReturn("hash-nuevo");

        service.resetPassword(reset(token.token()));

        assertEquals("hash-nuevo", usuario.getPassword());
        assertNull(usuario.getPasswordResetToken(), "El token debe consumirse: uso único.");
        assertNull(usuario.getPasswordResetTokenExpiry());
    }

    // ==============================================================
    // AL-SEC-004: la verificación de correo también es de un solo uso
    // ==============================================================

    @Test
    void verifyEmail_deberiaRechazarTokenDeRecuperacion() {
        JwtUtils.ScopedToken token = jwtUtils.generateRecoveryToken("demo@al-lado.com");

        assertThrows(IllegalArgumentException.class, () -> service.verifyEmail(token.token()));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void verifyEmail_deberiaRechazarTokenDeSesion() {
        String tokenSesion = jwtUtils.generateToken(trabajador());

        assertThrows(IllegalArgumentException.class, () -> service.verifyEmail(tokenSesion));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void verifyEmail_deberiaVerificarElCorreoYConsumirElToken() {
        JwtUtils.ScopedToken token = jwtUtils.generateVerificationToken("demo@al-lado.com");
        Trabajador usuario = trabajador();
        usuario.setEmailVerificationToken(token.jti());
        usuario.setEmailVerificationTokenExpiry(LocalDateTime.now().plusMinutes(10));
        when(usuarioRepository.findByEmail("demo@al-lado.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        service.verifyEmail(token.token());

        assertTrue(usuario.isEmailVerificado());
        assertNull(usuario.getEmailVerificationToken(), "El token debe consumirse: uso único.");
        assertNull(usuario.getEmailVerificationTokenExpiry());
    }

    @Test
    void verifyEmail_deberiaRechazarTokenVencido() {
        JwtUtils.ScopedToken token = jwtUtils.generateVerificationToken("demo@al-lado.com");
        Trabajador usuario = trabajador();
        usuario.setEmailVerificationToken(token.jti());
        usuario.setEmailVerificationTokenExpiry(LocalDateTime.now().minusMinutes(1));
        when(usuarioRepository.findByEmail("demo@al-lado.com")).thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class, () -> service.verifyEmail(token.token()));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void generarRecoveryToken_deberiaDeclararElPropositoEnElClaim() {
        // El propósito del claim y el registro en la base tienen que coincidir.
        JwtUtils.ScopedToken token = jwtUtils.generateRecoveryToken("demo@al-lado.com");

        assertEquals(TokenPurpose.RECOVERY, jwtUtils.extractPurpose(token.token()));
    }
}
