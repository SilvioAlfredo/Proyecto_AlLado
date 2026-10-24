package com.al_lado.al_lado_project.security.jwt;

import com.al_lado.al_lado_project.entity.usuario.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

/**
 * Emite JWT y comprueba su firma, vencimiento y usuario.
 *
 * <p><b>Secreto (AL-SEC-001):</b> la clave de firma se toma de la propiedad
 * {@code jwt.secret}, que en producción llega desde la variable de entorno
 * {@code JWT_SECRET}. No hay ningún valor por defecto en el código: si falta, la
 * aplicación no arranca. La clave debe tener al menos 32 bytes, que es lo que
 * exige HS256.</p>
 *
 * <p>Los tokens nuevos incluyen un identificador {@code jti}. La lista de tokens invalidados se
 * consulta en {@link JwtAuthenticationFilter}, no en esta clase. Los tokens antiguos sin jti no
 * pueden invalidarse individualmente.</p>
 *
 * <p><b>Propósito (AL-SEC-004):</b> todo token declara su {@link TokenPurpose} en el claim
 * {@code purpose}. Los tokens de un solo uso (recuperación y verificación) se emiten con
 * {@link #generateScopedToken} y caducan por sí solos; además, el {@code jti} queda registrado
 * en la base para poder invalidarlos al consumption.</p>
 *
 * <p><b>Identidad (AL-API-005):</b> el token de sesión incluye el claim {@code uid} con el
 * identificador del usuario, de modo que el cliente no necesita listar usuarios para
 * descubrir el suyo.</p>
 */
@Component
public class JwtUtils {

    /** Claim con el identificador del usuario. */
    public static final String CLAIM_USUARIO_ID = "uid";

    /** Claim con la finalidad del token. */
    public static final String CLAIM_PROPOSITO = "purpose";

    /** Longitud mínima de la clave en bytes (HS256 exige 256 bits). */
    private static final int LONGITUD_MINIMA_CLAVE = 32;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 horas en milisegundos
    private long jwtExpiration;

    @Value("${auth.token.recovery.ttl-minutes:30}")
    private long recoveryTtlMinutes;

    @Value("${auth.token.verification.ttl-minutes:1440}")
    private long verificationTtlMinutes;

    private SecretKey getSigningKey() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException(
                    "Falta la propiedad 'jwt.secret'. Definí la variable de entorno JWT_SECRET "
                            + "con una clave aleatoria de al menos 32 bytes.");
        }
        byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < LONGITUD_MINIMA_CLAVE) {
            throw new IllegalStateException(
                    "La propiedad 'jwt.secret' es demasiado corta (" + bytes.length + " bytes): "
                            + "se requieren al menos " + LONGITUD_MINIMA_CLAVE + " bytes para HS256.");
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    /**
     * Emite un JWT de sesión firmado con email, roles e identificador único.
     *
     * <p>La duración se configura con {@code jwt.expiration}; por defecto es de 24 horas. El
     * filtro recarga los roles desde la base en cada solicitud.</p>
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_PROPOSITO, TokenPurpose.SESSION.name());
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        // Identidad mínima: el id viaja en el token, el cliente no lo busca listando usuarios.
        if (userDetails instanceof Usuario usuario && usuario.getId() != null) {
            claims.put(CLAIM_USUARIO_ID, usuario.getId());
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Emite un token de un solo uso para un flujo concreto (recuperación o verificación).
     *
     * <p>El token lleva su {@link TokenPurpose}, un {@code jti} único y una expiración propia.
     * El llamador debe registrar el {@code jti} devuelto para poder invalidar el token al
     * consumirlo.</p>
     *
     * @param subject  usuario al que pertenece el token (email)
     * @param purpose  flujo para el que vale el token
     * @param minutes  minutos de vigencia
     * @return el token emitido y su identificador {@code jti}
     */
    public ScopedToken generateScopedToken(String subject, TokenPurpose purpose, long minutes) {
        String jti = UUID.randomUUID().toString();
        long vigencia = Math.max(1, minutes);
        String token = Jwts.builder()
                .setSubject(subject)
                .setId(jti)
                .claim(CLAIM_PROPOSITO, purpose.name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + vigencia * 60_000L))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        return new ScopedToken(token, jti);
    }

    /** Token de recuperación de contraseña, con la vigencia configurada. */
    public ScopedToken generateRecoveryToken(String email) {
        return generateScopedToken(email, TokenPurpose.RECOVERY, recoveryTtlMinutes);
    }

    /** Token de verificación de correo, con la vigencia configurada. */
    public ScopedToken generateVerificationToken(String email) {
        return generateScopedToken(email, TokenPurpose.VERIFY_EMAIL, verificationTtlMinutes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Identificador único del token (jti), usado para revocación. Null en tokens antiguos. */
    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Finalidad declarada por el token.
     *
     * @return el propósito, o {@code null} si el token no lo declara (tokens antiguos)
     */
    public TokenPurpose extractPurpose(String token) {
        String purpose = extractClaim(token, claims -> claims.get(CLAIM_PROPOSITO, String.class));
        if (purpose == null) {
            return null;
        }
        try {
            return TokenPurpose.valueOf(purpose);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Identificador del usuario al que pertenece el token.
     *
     * @return el id del usuario, o {@code null} si el token no lo declara
     */
    public Long extractUserId(String token) {
        Object uid = extractClaim(token, claims -> claims.get(CLAIM_USUARIO_ID));
        if (uid instanceof Number numero) {
            return numero.longValue();
        }
        if (uid instanceof String texto && !texto.isBlank()) {
            try {
                return Long.parseLong(texto);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Comprueba la firma, el vencimiento y que el usuario coincida.
     *
     * <p>No comprueba si el token fue invalidado al cerrar sesión. Esa consulta la realiza
     * {@link JwtAuthenticationFilter}.</p>
     *
     * @param token token recibido
     * @param userDetails principal ya cargado por {@code UserDetailsServiceImpl}
     * @return {@code true} si el subject coincide y no está vencido
     * @throws io.jsonwebtoken.JwtException
     *     si el token es malformado, la firma no coincide o está vencido (las lecturas
     *     de claims lanzan, no devuelven {@code null})
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Token emitido y su identificador {@code jti}.
     *
     * <p>El {@code jti} es lo que se registra en la base para invalidar el token al
     * consumirlo; el token completo nunca se persiste.</p>
     *
     * @param token token firmado
     * @param jti   identificador único del token
     */
    public record ScopedToken(String token, String jti) {
    }
}
