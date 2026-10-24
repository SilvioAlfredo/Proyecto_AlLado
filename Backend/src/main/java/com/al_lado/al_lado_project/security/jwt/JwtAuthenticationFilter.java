package com.al_lado.al_lado_project.security.jwt;

import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Autentica solicitudes con la cabecera {@code Authorization: Bearer <jwt>}.
 *
 * <p>Sin Bearer continúa sin autenticar. Un token inválido, vencido o cuyo usuario no puede
 * cargarse recibe 401. Los roles se obtienen de la base de datos.</p>
 *
 * <p>Si el token está revocado, no autentica y deja que el endpoint decida la respuesta. Los
 * errores del filtro ocurren antes del controller y no pasan por {@link
 * com.al_lado.al_lado_project.exception.GlobalExceptionHandler}.</p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenRevocadoRepository tokenRevocadoRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final TokenPurpose proposito;
        jwt = authHeader.substring(7);
        try {
            userEmail = jwtUtils.extractUsername(jwt);
            proposito = jwtUtils.extractPurpose(jwt);
        } catch (RuntimeException e) {
            // Token malformado, con firma inválida o expirado: 401 genérico
            // sin filtrar detalles internos ni continuar la cadena.
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado.");
            return;
        }

        // AL-SEC-004: sólo un token de SESIÓN es credencial de acceso. El token de
        // recuperación o el de verificación (los que llegan por correo) mantienen firma
        // y vencimiento válidos, pero no deben abrir sesión: si no, quien intercepta un
        // enlace de recuperación actuaría como el usuario. Los tokens antiguos sin el
        // claim purpose tampoco autentican: hay que volver a iniciar sesión. Si el token
        // no da usuario no hay nada que autenticar y la cadena sigue como antes.
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (proposito != TokenPurpose.SESSION) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado.");
                return;
            }

            final UserDetails userDetails;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            } catch (RuntimeException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado.");
                return;
            }

            if (jwtUtils.isTokenValid(jwt, userDetails) && !estaRevocado(jwt)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    /** Tokens antiguos sin jti no son revocables y se aceptan hasta su expiración. */
    private boolean estaRevocado(String jwt) {
        try {
            String jti = jwtUtils.extractTokenId(jwt);
            return jti != null && tokenRevocadoRepository.existsByJti(jti);
        } catch (RuntimeException e) {
            return true;
        }
    }
}