package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.admin.DashboardMetricsDTO;
import com.al_lado.al_lado_project.dto.usuario.AdministradorDTO;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.usuario.AdministradorMapper;
import com.al_lado.al_lado_project.repository.comunidad.DonacionRepository;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.service.usuario.AdministradorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * HU-18 y HU-19: Administrador gestiona usuarios y consulta panel de metricas.
 *
 * <p>Pruebas unitarias de {@link AdministradorServiceImpl}. Valida CRUD de
 * administradores y calculo de metricas del dashboard.</p>
 */
@ExtendWith(MockitoExtension.class)
class AdministradorServiceImplTest {

    @Mock
    private AdministradorRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AdministradorMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DonacionRepository donacionRepository;

    @Mock
    private SuscripcionRepository suscripcionRepository;

    @Mock
    private PostulacionRepository postulacionRepository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private OportunidadLaboralRepository oportunidadLaboralRepository;

    @Mock
    private Clock clock;

    private AdministradorService service;

    private Administrador administrador;
    private AdministradorDTO dto;

    @BeforeEach
    void setUp() {
        service = new AdministradorServiceImpl(
                repository, usuarioRepository, mapper, passwordEncoder,
                donacionRepository, suscripcionRepository, postulacionRepository,
                trabajadorRepository, oportunidadLaboralRepository, clock);

        administrador = new Administrador();
        administrador.setId(1L);
        administrador.setNombre("Admin Demo");
        administrador.setEmail("admin@demo.com");
        administrador.setTelefono("+549110000000");
        administrador.setNivelAcceso("SUPER");

        dto = new AdministradorDTO();
        dto.setId(1L);
        dto.setNombre("Admin Demo");
        dto.setEmail("admin@demo.com");
        dto.setTelefono("+549110000000");
        dto.setNivelAcceso("SUPER");
    }

    @Test
    void obtenerTodos_deberiaRetornarListaDeAdministradores() {
        when(repository.findAll()).thenReturn(List.of(administrador));
        when(mapper.toDTO(administrador)).thenReturn(dto);

        List<AdministradorDTO> resultado = service.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("Admin Demo", resultado.get(0).getNombre());
        verify(repository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_deberiaRetornarAdministradorExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(administrador));
        when(mapper.toDTO(administrador)).thenReturn(dto);

        AdministradorDTO resultado = service.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void obtenerPorId_deberiaRechazarSiAdministradorNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    void guardar_deberiaCrearNuevoAdministrador() {
        when(usuarioRepository.existsByEmail("admin@demo.com")).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(administrador);
        when(passwordEncoder.encode("password123")).thenReturn("hash-encriptado");
        when(repository.save(administrador)).thenReturn(administrador);
        when(mapper.toDTO(administrador)).thenReturn(dto);

        dto.setPassword("password123");
        AdministradorDTO resultado = service.guardar(dto);

        assertNotNull(resultado);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(repository, times(1)).save(administrador);
    }

    @Test
    void guardar_deberiaRechazarEmailDuplicado() {
        when(usuarioRepository.existsByEmail("admin@demo.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.guardar(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void eliminar_deberiaEliminarAdministradorExistente() {
        when(repository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_deberiaRechazarSiAdministradorNoExiste() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(999L));
    }

    @Test
    void obtenerMetricasDashboard_deberiaRetornarMetricasCompletas() {
        when(donacionRepository.sumTotalMontos()).thenReturn(BigDecimal.valueOf(50000));
        when(suscripcionRepository.countProVigentes(LocalDate.now(clock))).thenReturn(3L);
        when(postulacionRepository.count()).thenReturn(100L);
        when(trabajadorRepository.count()).thenReturn(50L);
        when(postulacionRepository.promedioRemuneracionPositiva()).thenReturn(15000.0);
        when(postulacionRepository.sumaRemuneracionCompletadas()).thenReturn(75000.0);
        when(postulacionRepository.countByEstadoIgnoreCase("COMPLETADA")).thenReturn(40L);
        when(oportunidadLaboralRepository.count()).thenReturn(20L);
        when(oportunidadLaboralRepository.findTendenciasPorEtiqueta()).thenReturn(List.of());
        when(usuarioRepository.countByHabilitado(true)).thenReturn(45L);
        when(usuarioRepository.countByHabilitado(false)).thenReturn(5L);
        when(clock.instant()).thenReturn(java.time.Instant.parse("2026-01-15T00:00:00Z"));
        when(clock.getZone()).thenReturn(java.time.ZoneId.of("UTC"));

        DashboardMetricsDTO resultado = service.obtenerMetricasDashboard();

        assertNotNull(resultado);
        assertEquals(100L, resultado.getMetricContratos());
        assertEquals(50L, resultado.getMetricTrabajadores());
        assertEquals(40L, resultado.getTrabajosCompletados());
        assertEquals(45L, resultado.getUsuariosActivos());
        assertEquals(5L, resultado.getUsuariosInactivos());
        assertNotNull(resultado.getPostulacionesPorEstado());
        assertTrue(resultado.getPostulacionesPorEstado().containsKey("PENDIENTE"));
        assertTrue(resultado.getPostulacionesPorEstado().containsKey("COMPLETADA"));
    }
}
