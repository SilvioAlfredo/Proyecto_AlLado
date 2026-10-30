package com.al_lado.al_lado_project.security.jwt;

import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AL-SEC-004: el propósito del token decide si sirve como credencial.
 *
 * <p>Sólo un token de SESIÓN autentica. Los que llegan por correo (recuperación y
 * verificación) conservan firma y vencimiento, pero no deben abrir sesión.</p>
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String BEARER = "Bearer tok-1";

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private TokenRevocadoRepository tokenRevocadoRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filtro;

    @BeforeEach
    void setUp() {
        filtro = new JwtAuthenticationFilter(jwtUtils, userDetailsService, tokenRevocadoRepository);
        when(request.getHeader("Authorization")).thenReturn(BEARER);
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private UserDetails usuario() {
        return new User("demo@al-lado.com", "x",
                List.of(new SimpleGrantedAuthority("ROLE_TRABAJADOR")));
    }

    @Test
    void tokenDeRecuperacionNoAutentica() throws Exception {
        when(jwtUtils.extractUsername("tok-1")).thenReturn("demo@al-lado.com");
        when(jwtUtils.extractPurpose("tok-1")).thenReturn(TokenPurpose.RECOVERY);

        filtro.doFilterInternal(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado.");
        verify(filterChain, never()).doFilter(any(), any());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Un token de recuperación no debe abrir sesión.");
    }

    @Test
    void tokenDeVerificacionNoAutentica() throws Exception {
        when(jwtUtils.extractUsername("tok-1")).thenReturn("demo@al-lado.com");
        when(jwtUtils.extractPurpose("tok-1")).thenReturn(TokenPurpose.VERIFY_EMAIL);

        filtro.doFilterInternal(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado.");
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Un token de verificación no debe abrir sesión.");
    }

    @Test
    void tokenAntiguoSinPropositoNoAutentica() throws Exception {
        when(jwtUtils.extractUsername("tok-1")).thenReturn("demo@al-lado.com");
        when(jwtUtils.extractPurpose("tok-1")).thenReturn(null);

        filtro.doFilterInternal(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado.");
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Un token sin el claim purpose no debe abrir sesión.");
    }

    @Test
    void tokenDeSesionValidoSiAutentica() throws Exception {
        UserDetails usuario = usuario();
        when(jwtUtils.extractUsername("tok-1")).thenReturn("demo@al-lado.com");
        when(jwtUtils.extractPurpose("tok-1")).thenReturn(TokenPurpose.SESSION);
        when(userDetailsService.loadUserByUsername("demo@al-lado.com")).thenReturn(usuario);
        when(jwtUtils.isTokenValid("tok-1", usuario)).thenReturn(true);
        when(jwtUtils.extractTokenId("tok-1")).thenReturn(null);

        filtro.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication(),
                "El token de sesión válido debe autenticar.");
        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void tokenDeSesionRevocadoNoAutentica() throws Exception {
        UserDetails usuario = usuario();
        when(jwtUtils.extractUsername("tok-1")).thenReturn("demo@al-lado.com");
        when(jwtUtils.extractPurpose("tok-1")).thenReturn(TokenPurpose.SESSION);
        when(userDetailsService.loadUserByUsername("demo@al-lado.com")).thenReturn(usuario);
        when(jwtUtils.isTokenValid("tok-1", usuario)).thenReturn(true);
        when(jwtUtils.extractTokenId("tok-1")).thenReturn("jti-1");
        when(tokenRevocadoRepository.existsByJti("jti-1")).thenReturn(true);

        filtro.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Un token revocado no debe autenticar.");
        verify(filterChain).doFilter(request, response);
    }
}
