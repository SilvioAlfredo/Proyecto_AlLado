package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.comunidad.Donacion;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.DonacionMapper;
import com.al_lado.al_lado_project.repository.comunidad.DonacionRepository;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.DonacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * HU-06 CA-02 y HU-16: Verificacion de transferencias y donaciones para activar PRO.
 *
 * <p>Pruebas unitarias de {@link DonacionServiceImpl}. Valida crear donacion,
 * verificar transferencia por admin, comparacion de monto y asunto, y
 * activacion de PRO.</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DonacionServiceImplTest {

    @Mock
    private DonacionRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SuscripcionRepository suscripcionRepository;

    @Mock
    private DonacionMapper mapper;

    @Mock
    private Clock clock;

    private DonacionService service;

    private Usuario usuario;
    private DonacionDTO dto;
    private Donacion entidad;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new DonacionServiceImpl(
                repository, usuarioRepository, suscripcionRepository, mapper, clock);

        usuario = new Trabajador("Usuario Demo", "usuario@demo.com", "password");
        usuario.setId(1L);

        dto = new DonacionDTO();
        dto.setId(1L);
        dto.setMonto(new BigDecimal("15000.0"));
        dto.setAsuntoTransferencia("APORTE PRO");
        dto.setEstado("PENDIENTE");
        dto.setFechaDonacion(LocalDateTime.of(2026, 1, 15, 10, 0));

        var usuarioResumen = new UsuarioResumenDTO();
        usuarioResumen.setId(1L);
        usuarioResumen.setNombre("Usuario Demo");
        usuarioResumen.setEmail("usuario@demo.com");
        dto.setUsuario(usuarioResumen);

        entidad = new Donacion();
        entidad.setId(1L);
        entidad.setMonto(new BigDecimal("15000.0"));
        entidad.setAsuntoTransferencia("APORTE PRO");
        entidad.setEstado("PENDIENTE");
        entidad.setUsuario(usuario);
    }

    private void autenticarComoUsuario() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    private void autenticarComoOtroUsuario() {
        Trabajador otro = new Trabajador("Otro", "otro@demo.com", "password");
        otro.setId(2L);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(otro, null, otro.getAuthorities()));
    }

    private void autenticarComoAdmin() {
        Administrador admin = new Administrador("Admin", "admin@demo.com", "password", "SUPER");
        admin.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void crear_deberiaRegistrarDonacionPendiente() {
        autenticarComoUsuario();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(mapper.toEntity(dto)).thenReturn(entidad);
        when(repository.save(any(Donacion.class))).thenReturn(entidad);
        when(mapper.toDTO(entidad)).thenReturn(dto);

        DonacionDTO resultado = service.crear(dto);

        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(repository, times(1)).save(any(Donacion.class));
    }

    @Test
    void crear_deberiaRechazarMontoCeroOMenor() {
        SecurityContextHolder.clearContext();
        dto.setMonto(BigDecimal.ZERO);

        assertThrows(BusinessRuleException.class, () -> service.crear(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void crear_deberiaRechazarSiUsuarioNoExiste() {
        autenticarComoUsuario();
        when(repository.save(any(Donacion.class))).thenReturn(entidad);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.crear(dto));
    }

    @Test
    void crear_deberiaRechazarAccesoAjeno() {
        SecurityContextHolder.clearContext();
        autenticarComoOtroUsuario();
        when(repository.save(any(Donacion.class))).thenReturn(entidad);

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> service.crear(dto));
    }

    @Test
    void verificarTransferencia_deberiaActivarPROCuandoDatosCoinciden() {
        autenticarComoAdmin();
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(entidad));
        when(repository.save(entidad)).thenReturn(entidad);
        dto.setEstado("VERIFICADO");
        when(mapper.toDTO(entidad)).thenReturn(dto);

        DonacionDTO resultado = service.verificarTransferencia(1L, "APORTE PRO", new BigDecimal("15000.0"));

        assertEquals("VERIFICADO", resultado.getEstado());
        verify(repository, times(1)).save(entidad);
    }

    @Test
    void verificarTransferencia_deberiaRechazarSiMontoNoCoincide() {
        autenticarComoAdmin();
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(entidad));

        assertThrows(BusinessRuleException.class,
                () -> service.verificarTransferencia(1L, "APORTE PRO", new BigDecimal("20000.0")));
        verify(repository, never()).save(entidad);
    }

    @Test
    void verificarTransferencia_deberiaRechazarSiAsuntoNoCoincide() {
        autenticarComoAdmin();
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(entidad));

        assertThrows(BusinessRuleException.class,
                () -> service.verificarTransferencia(1L, "OTRO ASUNTO", new BigDecimal("15000.0")));
    }

    @Test
    void verificarTransferencia_deberiaRechazarSiDonacionYaProcesada() {
        autenticarComoAdmin();
        entidad.setEstado("VERIFICADO");
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(entidad));

        assertThrows(BusinessRuleException.class,
                () -> service.verificarTransferencia(1L, "APORTE PRO", new BigDecimal("15000.0")));
    }

    @Test
    void verificarTransferencia_deberiaRechazarSiDonacionNoExiste() {
        autenticarComoAdmin();
        when(repository.findByIdForUpdate(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.verificarTransferencia(999L, "APORTE PRO", new BigDecimal("15000.0")));
    }

    @Test
    void verificarTransferencia_deberiaRechazarSiNoEsAdmin() {
        SecurityContextHolder.clearContext();
        autenticarComoOtroUsuario();

        assertThrows(AccessDeniedException.class,
                () -> service.verificarTransferencia(1L, "APORTE PRO", new BigDecimal("15000.0")));
    }

    @Test
    void obtenerPorUsuario_deberiaRetornarDonacionesDelUsuario() {
        autenticarComoUsuario();
        Page<Donacion> pagina = new PageImpl<>(List.of(entidad), PageRequest.of(0, 10), 1);
        when(repository.findByUsuarioId(eq(1L), any(Pageable.class))).thenReturn(pagina);
        when(mapper.toDTO(entidad)).thenReturn(dto);

        Page<DonacionDTO> resultado = service.obtenerPorUsuario(1L, PageRequest.of(0, 10));

        assertEquals(1, resultado.getContent().size());
        assertEquals(15000.0, resultado.getContent().get(0).getMonto().doubleValue());
    }
}
