package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.NotificacionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Notificacion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.mappers.comunidad.NotificacionMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import com.al_lado.al_lado_project.repository.comunidad.NotificacionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceImplTest {

    @Mock
    private NotificacionRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private NotificacionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new NotificacionServiceImpl(repository, usuarioRepository,
                new NotificacionMapper(new UsuarioResumenMapper()));
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("TC-NOTIF-001: el titular marca una notificación individual como leída")
    void marcarLeida_deberiaPersistirCuandoEsTitular() {
        Trabajador titular = trabajador(7L);
        Notificacion notificacion = notificacion(titular, false);
        when(repository.findById(11L)).thenReturn(Optional.of(notificacion));
        when(repository.save(notificacion)).thenReturn(notificacion);
        iniciarSesion(titular);

        NotificacionDTO resultado = service.marcarLeida(11L);

        assertTrue(resultado.isLeida());
        verify(repository).save(notificacion);
    }

    @Test
    @DisplayName("TC-SEC-005: un tercero no puede marcar la notificación ajena")
    void marcarLeida_deberiaRechazarNotificacionAjena() {
        Notificacion notificacion = notificacion(trabajador(8L), false);
        when(repository.findById(11L)).thenReturn(Optional.of(notificacion));
        iniciarSesion(trabajador(7L));

        assertThrows(AccessDeniedException.class, () -> service.marcarLeida(11L));
        verify(repository, never()).save(notificacion);
    }

    @Test
    @DisplayName("TC-NOTIF-002: el titular marca todas sus notificaciones como leídas")
    void marcarTodasLeidas_deberiaActualizarSoloLasNoLeidasDelTitular() {
        Trabajador titular = trabajador(7L);
        Notificacion pendiente = notificacion(titular, false);
        Notificacion yaLeida = notificacion(titular, true);
        when(repository.findByDestinatarioIdOrderByFechaCreacionDesc(7L))
                .thenReturn(List.of(pendiente, yaLeida));
        iniciarSesion(titular);

        service.marcarTodasLeidas(7L);

        assertTrue(pendiente.isLeida());
        assertTrue(yaLeida.isLeida());
    }

    @Test
    @DisplayName("TC-NOTIF-005: el contador devuelve las notificaciones pendientes del titular")
    void contarNoLeidas_deberiaDevolverElConteoDelTitular() {
        when(repository.countByDestinatarioIdAndLeidaFalse(7L)).thenReturn(2L);
        iniciarSesion(trabajador(7L));

        assertEquals(2L, service.contarNoLeidas(7L));
    }

    @Test
    @DisplayName("TC-SEC-004: un tercero no puede listar notificaciones ajenas")
    void listarPorUsuario_deberiaRechazarAUnTercero() {
        iniciarSesion(trabajador(7L));

        assertThrows(AccessDeniedException.class, () -> service.listarPorUsuario(8L));
    }

    private void iniciarSesion(Trabajador trabajador) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(trabajador, null, trabajador.getAuthorities()));
    }

    private static Trabajador trabajador(Long id) {
        Trabajador trabajador = new Trabajador();
        trabajador.setId(id);
        trabajador.setNombre("Trabajador " + id);
        trabajador.setEmail("trabajador" + id + "@example.com");
        trabajador.setPassword("test");
        return trabajador;
    }

    private static Notificacion notificacion(Trabajador destinatario, boolean leida) {
        Notificacion notificacion = new Notificacion(destinatario, "Aviso", "Mensaje",
                Notificacion.TIPO_SISTEMA, null);
        notificacion.setLeida(leida);
        return notificacion;
    }
}