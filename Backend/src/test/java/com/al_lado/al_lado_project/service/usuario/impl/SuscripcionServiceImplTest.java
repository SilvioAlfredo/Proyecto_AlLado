package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.usuario.SuscripcionMapper;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.springframework.security.access.AccessDeniedException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

/**
 * HU-16: Adquirir el nivel PRO realizando un aporte por transferencia.
 *
 * <p>Pruebas unitarias de {@link SuscripcionServiceImpl}. Valida creacion de
 * planes BASICO/PRO, restriccion de PRO solo por administrador, reactivacion,
 * cancelacion, puedePublicarOferta y esProVigente.</p>
 */
@ExtendWith(MockitoExtension.class)
class SuscripcionServiceImplTest {

    @Mock
    private SuscripcionRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private OportunidadLaboralRepository oportunidadLaboralRepository;

    @Mock
    private SuscripcionMapper mapper;

    @Mock
    private Clock clock;

    private SuscripcionService service;

    private Usuario usuario;
    private SuscripcionDTO dto;
    private Suscripcion entidad;

    @BeforeEach
    void setUp() {
        service = new SuscripcionServiceImpl(
                repository, usuarioRepository, oportunidadLaboralRepository, mapper, clock);

        usuario = new Usuario("Usuario Demo", "usuario@demo.com", "password");
        usuario.setId(1L);

        dto = new SuscripcionDTO();
        var usuarioResumen = new UsuarioResumenDTO();
        usuarioResumen.setId(1L);
        usuarioResumen.setNombre("Usuario Demo");
        usuarioResumen.setEmail("usuario@demo.com");
        dto.setUsuario(usuarioResumen);
        dto.setNivel("BASICO");
        dto.setFechaInicio(LocalDate.of(2026, 1, 1));
        dto.setActiva(true);

        entidad = new Suscripcion();
        entidad.setId(1L);
        entidad.setUsuario(usuario);
        entidad.setNivel("BASICO");
        entidad.setActiva(true);
    }

    @Test
    void crear_deberiaCrearPlanBasicoParaUsuarioNoAdmin() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);
            mocked.when(SecurityUtils::isAdmin()).thenReturn(false);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(repository.existsByUsuarioId(1L)).thenReturn(false);
            when(clock.instant()).thenReturn(Instant.parse("2026-01-15T00:00:00Z"));
            when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
            when(mapper.toEntity(dto)).thenReturn(entidad);
            when(repository.save(entidad)).thenReturn(entidad);
            when(mapper.toDTO(entidad)).thenReturn(dto);

            SuscripcionDTO resultado = service.crear(dto);

            assertNotNull(resultado);
            assertEquals("BASICO", resultado.getNivel());
            verify(repository, times(1)).save(entidad);
        }
    }

    @Test
    void crear_deberiaRechazarPROParaUsuarioNoAdmin() {
        dto.setNivel("PRO");
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);
            mocked.when(SecurityUtils::isAdmin()).thenReturn(false);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

            assertThrows(AccessDeniedException.class, () -> service.crear(dto));
        }
    }

    @Test
    void crear_deberiaPermitirPROParaAdmin() {
        dto.setNivel("PRO");
        dto.setFechaFin(LocalDate.of(2027, 1, 1));
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);
            mocked.when(SecurityUtils::isAdmin()).thenReturn(true);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(repository.existsByUsuarioId(1L)).thenReturn(false);
            when(mapper.toEntity(dto)).thenReturn(entidad);
            when(repository.save(entidad)).thenReturn(entidad);
            when(mapper.toDTO(entidad)).thenReturn(dto);

            SuscripcionDTO resultado = service.crear(dto);

            assertNotNull(resultado);
            assertEquals("PRO", resultado.getNivel());
            verify(repository, times(1)).save(entidad);
        }
    }

    @Test
    void crear_deberiaReactivarSuscripcionExistente() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);
            mocked.when(SecurityUtils::isAdmin()).thenReturn(false);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(repository.existsByUsuarioId(1L)).thenReturn(true);
            when(repository.findByUsuarioId(1L)).thenReturn(Optional.of(entidad));
            when(clock.instant()).thenReturn(Instant.parse("2026-01-15T00:00:00Z"));
            when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
            when(repository.save(entidad)).thenReturn(entidad);
            when(mapper.toDTO(entidad)).thenReturn(dto);

            SuscripcionDTO resultado = service.crear(dto);

            assertNotNull(resultado);
            verify(repository, times(1)).save(entidad);
        }
    }

    @Test
    void crear_deberiaRechazarNivelInvalido() {
        dto.setNivel("PREMIUM");
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);

            assertThrows(BusinessRuleException.class, () -> service.crear(dto));
        }
    }

    @Test
    void cancelar_deberiaDesactivarSuscripcion() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);
            when(repository.findById(1L)).thenReturn(Optional.of(entidad));
            when(repository.save(entidad)).thenReturn(entidad);
            when(mapper.toDTO(entidad)).thenReturn(dto);

            SuscripcionDTO resultado = service.cancelar(1L);

            assertFalse(entidad.isActiva());
            verify(repository, times(1)).save(entidad);
        }
    }

    @Test
    void cancelar_deberiaRechazarAccesoAjeno() {
        entidad.getUsuario().setId(2L);
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);
            when(repository.findById(1L)).thenReturn(Optional.of(entidad));

            assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> service.cancelar(1L));
        }
    }

    @Test
    void esProVigente_deberiaRetornarTrueParaPROVigente() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            Suscripcion pro = new Suscripcion();
            pro.setNivel("PRO");
            pro.setActiva(true);
            pro.setFechaInicio(LocalDate.of(2026, 1, 1));
            pro.setFechaFin(LocalDate.of(2027, 1, 1));
            when(repository.findByUsuarioId(1L)).thenReturn(Optional.of(pro));
            when(clock.instant()).thenReturn(Instant.parse("2026-06-01T00:00:00Z"));
            when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

            boolean resultado = service.esProVigente(1L);

            assertTrue(resultado);
        }
    }

    @Test
    void puedePublicarOferta_deberiaPermitirTresOfertasParaBasico() {
        Suscripcion basico = new Suscripcion();
        basico.setNivel("BASICO");
        basico.setActiva(true);
        basico.setFechaInicio(LocalDate.of(2026, 1, 1));
        basico.setFechaFin(null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.findByUsuarioId(1L)).thenReturn(Optional.of(basico));
        when(oportunidadLaboralRepository.countByAutorIdAndActivaAndFechaCreacionBetween(
                anyLong(), eq(true), any(), any())).thenReturn(2L);

        boolean resultado = service.puedePublicarOferta(1L);

        assertTrue(resultado);
    }

    @Test
    void puedePublicarOferta_deberiaRechazarCuartoOfertaParaBasico() {
        Suscripcion basico = new Suscripcion();
        basico.setNivel("BASICO");
        basico.setActiva(true);
        basico.setFechaInicio(LocalDate.of(2026, 1, 1));
        basico.setFechaFin(null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.findByUsuarioId(1L)).thenReturn(Optional.of(basico));
        when(oportunidadLaboralRepository.countByAutorIdAndActivaAndFechaCreacionBetween(
                anyLong(), eq(true), any(), any())).thenReturn(3L);

        boolean resultado = service.puedePublicarOferta(1L);

        assertFalse(resultado);
    }

    @Test
    void puedePublicarOferta_deberiaPermitirIlimitadoParaPROVigente() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            Suscripcion pro = new Suscripcion();
            pro.setNivel("PRO");
            pro.setActiva(true);
            pro.setFechaInicio(LocalDate.of(2026, 1, 1));
            pro.setFechaFin(LocalDate.of(2027, 1, 1));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(repository.findByUsuarioId(1L)).thenReturn(Optional.of(pro));
            when(clock.instant()).thenReturn(Instant.parse("2026-06-01T00:00:00Z"));
            when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

            boolean resultado = service.puedePublicarOferta(1L);

            assertTrue(resultado);
        }
    }
}
