package com.al_lado.al_lado_project.service.comunidad.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.al_lado.al_lado_project.dto.comunidad.NotificacionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Notificacion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.mappers.comunidad.NotificacionMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import com.al_lado.al_lado_project.repository.comunidad.NotificacionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.service.comunidad.NotificacionService;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceImplTest {

    @Mock private NotificacionRepository notificacionRepository;
    @Mock private UsuarioRepository usuarioRepository;
    private final NotificacionMapper mapper = new NotificacionMapper(new UsuarioResumenMapper());
    private NotificacionService service;

    private Trabajador usuario;
    private Notificacion notificacion;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new NotificacionServiceImpl(notificacionRepository, usuarioRepository, mapper);

        usuario = new Trabajador();
        usuario.setId(1L);
        usuario.setNombre("Usuario Test");
        usuario.setEmail("usuario@test.com");
        usuario.setPassword("x");

        notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setDestinatario(usuario);
        notificacion.setTitulo("Nueva postulación");
        notificacion.setLeida(false);

        // La sesion es del destinatario de la notificacion.
        autenticarComo(usuario);
    }

    @AfterEach
    void limpiarContexto() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void marcarLeida_deberiaMarcarCuandoEsElTitular() {
        // Dado
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        // Cuando
        NotificacionDTO resultado = service.marcarLeida(1L);

        // Entonces
        assertNotNull(resultado);
        assertTrue(notificacion.isLeida());
        verify(notificacionRepository, times(1)).save(notificacion);
    }

    @Test
    void marcarLeida_deberiaRechazarCuandoNoEsElTitular() {
        // Dado: la notificacion es del usuario 1 y la sesion es de otro usuario.
        Trabajador otroUsuario = new Trabajador();
        otroUsuario.setId(99L);
        otroUsuario.setEmail("otro@test.com");
        otroUsuario.setPassword("x");
        autenticarComo(otroUsuario);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));

        // Cuando
        assertThrows(AccessDeniedException.class, () -> service.marcarLeida(1L));

        // Entonces
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    void contarNoLeidas_deberiaRetornarConteoCorrecto() {
        // Dado
        when(notificacionRepository.countByDestinatarioIdAndLeidaFalse(1L)).thenReturn(3L);

        // Cuando
        long resultado = service.contarNoLeidas(1L);

        // Entonces
        assertEquals(3, resultado);
    }

    @Test
    void marcarTodasLeidas_deberiaMarcarLasPendientesDelUsuario() {
        // Dado
        Notificacion otra = new Notificacion();
        otra.setId(2L);
        otra.setDestinatario(usuario);
        otra.setTitulo("Otra notificación");
        when(notificacionRepository.findByDestinatarioIdOrderByFechaCreacionDesc(1L))
                .thenReturn(List.of(notificacion, otra));

        // Cuando
        service.marcarTodasLeidas(1L);

        // Entonces
        assertTrue(notificacion.isLeida());
        assertTrue(otra.isLeida());
    }

    private void autenticarComo(Usuario u) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities()));
    }
}
