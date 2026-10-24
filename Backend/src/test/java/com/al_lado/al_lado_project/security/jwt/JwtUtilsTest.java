package com.al_lado.al_lado_project.security.jwt;

import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RF-01 / HU-04 CA-01 · AL-SEC-001 / AL-SEC-004 / AL-API-005: firma, propósito e identidad de los tokens.
 *
 * <p>Se prueba la clase real (sin mocks) para verificar de verdad la firma HMAC, la
 * expiración y los claims.</p>
 */
class JwtUtilsTest {

    private static final String SECRETO = "clave-de-pruebas-de-al-lado-0123456789abcdef";

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secretKey", SECRETO);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpiration", 3_600_000L);
        ReflectionTestUtils.setField(jwtUtils, "recoveryTtlMinutes", 30L);
        ReflectionTestUtils.setField(jwtUtils, "verificationTtlMinutes", 1440L);
    }

    private Trabajador trabajador(Long id) {
        Trabajador t = new Trabajador();
        t.setId(id);
        t.setNombre("Demo");
        t.setEmail("demo@al-lado.com");
        t.setPassword("hash");
        return t;
    }

    @Test
    void generarToken_deberiaDeclararElPropositoSession() {
        String token = jwtUtils.generateToken(trabajador(7L));

        assertEquals(TokenPurpose.SESSION, jwtUtils.extractPurpose(token));
    }

    @Test
    void generarToken_deberiaIncluirLaIdentidadDelUsuario() {
        // AL-API-005: el id viaja en el token, el cliente no lista usuarios para saberlo.
        String token = jwtUtils.generateToken(trabajador(7L));

        assertEquals(7L, jwtUtils.extractUserId(token));
    }

    @Test
    void generarRecoveryToken_deberiaDeclararPropositoEIdentificador() {
        // AL-SEC-004: el token de recuperación no es un token de sesión.
        JwtUtils.ScopedToken scoped = jwtUtils.generateRecoveryToken("demo@al-lado.com");

        assertNotNull(scoped.jti());
        assertEquals(TokenPurpose.RECOVERY, jwtUtils.extractPurpose(scoped.token()));
        assertEquals(scoped.jti(), jwtUtils.extractTokenId(scoped.token()));
        assertEquals("demo@al-lado.com", jwtUtils.extractUsername(scoped.token()));
    }

    @Test
    void generateVerificationToken_deberiaDeclararPropositoEIdentificador() {
        JwtUtils.ScopedToken scoped = jwtUtils.generateVerificationToken("demo@al-lado.com");

        assertEquals(TokenPurpose.VERIFY_EMAIL, jwtUtils.extractPurpose(scoped.token()));
        assertEquals(scoped.jti(), jwtUtils.extractTokenId(scoped.token()));
    }

    @Test
    void generarRecoveryToken_deberiaGenerarIdentificadoresDistintos() {
        JwtUtils.ScopedToken primero = jwtUtils.generateRecoveryToken("demo@al-lado.com");
        JwtUtils.ScopedToken segundo = jwtUtils.generateRecoveryToken("demo@al-lado.com");

        assertTrue(!primero.jti().equals(segundo.jti()),
                "Cada token emitido debe tener un identificador propio (consumo único).");
    }

    @Test
    void generarRecoveryToken_deberiaCaducarEnTreintaMinutos() {
        JwtUtils.ScopedToken scoped = jwtUtils.generateRecoveryToken("demo@al-lado.com");
        Date expiracion = jwtUtils.extractExpiration(scoped.token());

        long minutos = (expiracion.getTime() - System.currentTimeMillis()) / 60000L;
        assertTrue(minutos <= 30 && minutos >= 29,
                "El token de recuperación debe caducar en 30 minutos, no en " + minutos);
    }

    @Test
    void generarToken_deberiaFallarSinSecretoConfigurado() {
        // AL-SEC-001: sin secreto configurado la aplicación no puede firmar tokens.
        JwtUtils sinSecreto = new JwtUtils();
        ReflectionTestUtils.setField(sinSecreto, "secretKey", "  ");

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> sinSecreto.generateToken(trabajador(1L)));
        assertTrue(error.getMessage().contains("jwt.secret"),
                "El error debe indicar qué propiedad falta.");
    }

    @Test
    void generarToken_deberiaFallarConSecretoCorto() {
        // AL-SEC-001: HS256 exige al menos 256 bits de clave.
        JwtUtils corto = new JwtUtils();
        ReflectionTestUtils.setField(corto, "secretKey", "corto");

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> corto.generateToken(trabajador(1L)));
        assertTrue(error.getMessage().contains("32 bytes"),
                "El error debe indicar la longitud mínima de la clave.");
    }

    @Test
    void extraerUsername_deberiaRechazarTokenDeOtraClave() {
        JwtUtils otra = new JwtUtils();
        ReflectionTestUtils.setField(otra, "secretKey", "otra-clave-de-pruebas-0123456789abcdef");
        String tokenAjeno = otra.generateToken(trabajador(3L));

        assertThrows(JwtException.class, () -> jwtUtils.extractUsername(tokenAjeno));
    }

    @Test
    void extraerProposito_deberiaRechazarTokenMalformado() {
        // Un token que no es un JWT no se puede leer: falla el parser.
        assertThrows(JwtException.class, () -> jwtUtils.extractPurpose("no-es-un-token"));
    }
}
