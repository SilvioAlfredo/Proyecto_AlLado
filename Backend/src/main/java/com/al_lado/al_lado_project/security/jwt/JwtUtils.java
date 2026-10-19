package com.al_lado.al_lado_project.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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
 * <p>Los tokens nuevos incluyen un identificador {@code jti}. La lista de tokens invalidados se
 * consulta en {@link JwtAuthenticationFilter}, no en esta clase. Los tokens antiguos sin jti no
 * pueden invalidarse individualmente.</p>
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret:ClaveSecretaMuySeguraParaFirmarTokensEnAlLadoProject123456}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 horas en milisegundos
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Emite un JWT firmado con email, roles e identificador único.
     *
     * <p>La duración se configura con {@code jwt.expiration}; por defecto es de 24 horas. El
     * filtro recarga los roles desde la base en cada solicitud.</p>
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
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
}