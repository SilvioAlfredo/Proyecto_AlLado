package com.al_lado.al_lado_project.service.usuario.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.al_lado.al_lado_project.dto.usuario.UsuarioDTO;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.perfil.HorarioDisponibilidadMapper;
import com.al_lado.al_lado_project.mappers.perfil.PortafolioItemMapper;
import com.al_lado.al_lado_project.mappers.perfil.PreferenciaNotificacionMapper;
import com.al_lado.al_lado_project.mappers.trabajo.EtiquetaMapper;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionResumenMapper;
import com.al_lado.al_lado_project.mappers.usuario.EmpleadorMapper;
import com.al_lado.al_lado_project.mappers.usuario.TrabajadorMapper;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.service.usuario.EmpleadorService;
import com.al_lado.al_lado_project.service.usuario.TrabajadorService;
import com.al_lado.al_lado_project.service.usuario.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private TrabajadorRepository trabajadorRepository;
    @Mock private EmpleadorRepository empleadorRepository;
    @Mock private EmpleadorMapper empleadorMapper;
    @Mock private TrabajadorService trabajadorService;
    @Mock private EmpleadorService empleadorService;

    // Sub-mappers del TrabajadorMapper real: con colecciones vacías no se invocan.
    @Mock private EtiquetaMapper etiquetaMapper;
    @Mock private PostulacionResumenMapper postulacionResumenMapper;
    @Mock private PortafolioItemMapper portafolioItemMapper;
    @Mock private HorarioDisponibilidadMapper horarioDisponibilidadMapper;
    @Mock private PreferenciaNotificacionMapper preferenciaNotificacionMapper;

    private UsuarioService service;

    private Trabajador usuario;

    @BeforeEach
    void setUp() {
        // Orden real del constructor de UsuarioServiceImpl (@RequiredArgsConstructor).
        TrabajadorMapper trabajadorMapper = new TrabajadorMapper(etiquetaMapper, postulacionResumenMapper,
                portafolioItemMapper, horarioDisponibilidadMapper, preferenciaNotificacionMapper);
        service = new UsuarioServiceImpl(usuarioRepository, trabajadorRepository, empleadorRepository,
                trabajadorMapper, empleadorMapper, trabajadorService, empleadorService);

        // Usuario es abstracta: se usa la subclase concreta Trabajador.
        usuario = new Trabajador("Usuario Test", "usuario@test.com", "password");
        usuario.setId(1L);
        usuario.setHabilitado(true);
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    /** Inicia sesión como el propio usuario (titular) para la proyección AL-SEC-006. */
    private void iniciarSesion(Trabajador usuario) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    @Test
    void obtenerPorId_deberiaRetornarUsuarioCuandoExiste() {
        // Dado
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(usuario));
        iniciarSesion(usuario);

        // Cuando
        UsuarioDTO resultado = service.obtenerPorId(1L);

        // Entonces
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("usuario@test.com", resultado.getEmail());
    }

    @Test
    void obtenerPorId_deberiaRechazarCuandoUsuarioNoExiste() {
        // Dado
        when(trabajadorRepository.findById(999L)).thenReturn(Optional.empty());
        when(empleadorRepository.findById(999L)).thenReturn(Optional.empty());

        // Cuando
        assertThrows(ResourceNotFoundException.class, () -> service.obtenerPorId(999L));

        // Entonces
        verify(trabajadorRepository, never()).save(any());
    }

    @Test
    void deshabilitar_deberiaDeshabilitarUsuarioCuandoEsAdmin() {
        // Dado
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Trabajador.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        service.deshabilitar(1L);

        // Entonces
        assertFalse(usuario.isHabilitado());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void habilitar_deberiaHabilitarUsuarioCuandoEsAdmin() {
        // Dado
        usuario.setHabilitado(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Trabajador.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        service.habilitar(1L);

        // Entonces
        assertTrue(usuario.isHabilitado());
        verify(usuarioRepository, times(1)).save(usuario);
    }
}
