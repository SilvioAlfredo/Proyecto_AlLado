package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.PublicidadDTO;
import com.al_lado.al_lado_project.entity.comunidad.Publicidad;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.PublicidadMapper;
import com.al_lado.al_lado_project.repository.comunidad.PublicidadRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.PublicidadService;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * HU-15: Publicar anuncios de comercio en la plataforma.
 *
 * <p>Pruebas unitarias de {@link PublicidadServiceImpl}. Valida crear,
 * actualizar, eliminar, listar vigentes, requisito de PRO vigente y
 * titularidad del anunciante.</p>
 */
@ExtendWith(MockitoExtension.class)
class PublicidadServiceImplTest {

    @Mock
    private PublicidadRepository repository;

    @Mock
    private EmpleadorRepository empleadorRepository;

    @Mock
    private PublicidadMapper mapper;

    @Mock
    private SuscripcionService suscripcionService;

    private PublicidadService service;

    private Empleador empleador;
    private PublicidadDTO dto;
    private Publicidad entidad;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new PublicidadServiceImpl(repository, empleadorRepository, mapper, suscripcionService);

        empleador = new Empleador("Empleador Demo", "empleador@demo.com", "password");
        empleador.setId(1L);

        dto = new PublicidadDTO();
        dto.setId(null);
        dto.setTitulo("Mi Comercio");
        dto.setContenido("Descripcion del anuncio");
        dto.setUrlImagen("https://comercio.com/img.png");
        dto.setUrlDestino("https://comercio.com");
        dto.setFechaInicio(LocalDate.of(2026, 1, 1));
        dto.setFechaFin(LocalDate.of(2026, 12, 31));
        dto.setActiva(true);

        var anunciante = new com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO();
        anunciante.setId(1L);
        anunciante.setNombre("Empleador Demo");
        anunciante.setEmail("empleador@demo.com");
        dto.setAnunciante(anunciante);

        entidad = new Publicidad();
        entidad.setId(1L);
        entidad.setTitulo("Mi Comercio");
        entidad.setAnunciante(empleador);
    }

    private void autenticarComoEmpleador() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(empleador, null, empleador.getAuthorities()));
    }

    @Test
    void obtenerVigentes_deberiaRetornarAnunciosVigentes() {
        autenticarComoEmpleador();
        when(repository.buscarPublicidadesVigentes(LocalDate.now())).thenReturn(List.of(entidad));
        when(mapper.toDTO(entidad)).thenReturn(dto);

        List<PublicidadDTO> resultado = service.obtenerVigentes();

        assertEquals(1, resultado.size());
        assertEquals("Mi Comercio", resultado.get(0).getTitulo());
    }

    @Test
    void guardar_deberiaCrearPublicidadConPROVigente() {
        autenticarComoEmpleador();
        when(suscripcionService.esProVigente(1L)).thenReturn(true);
        when(empleadorRepository.findById(1L)).thenReturn(Optional.of(empleador));
        when(mapper.toEntity(dto)).thenReturn(entidad);
        when(repository.save(entidad)).thenReturn(entidad);
        when(mapper.toDTO(entidad)).thenReturn(dto);

        PublicidadDTO resultado = service.guardar(dto);

        assertNotNull(resultado);
        assertEquals("Mi Comercio", resultado.getTitulo());
        verify(repository, times(1)).save(entidad);
    }

    @Test
    void guardar_deberiaRechazarSiAnuncianteNoTienePROVigente() {
        autenticarComoEmpleador();
        when(suscripcionService.esProVigente(1L)).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> service.guardar(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void guardar_deberiaRechazarSiFechasInvalidas() {
        dto.setFechaFin(LocalDate.of(2025, 1, 1));
        autenticarComoEmpleador();

        assertThrows(BusinessRuleException.class, () -> service.guardar(dto));
    }

    @Test
    void guardar_deberiaRechazarSiAnuncianteNulo() {
        dto.setAnunciante(null);
        autenticarComoEmpleador();

        assertThrows(BusinessRuleException.class, () -> service.guardar(dto));
    }

    @Test
    void guardar_deberiaRechazarAccesoAjeno() {
        Empleador otro = new Empleador("Otro", "otro@demo.com", "password");
        otro.setId(2L);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(otro, null, otro.getAuthorities()));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.guardar(dto));
    }

    @Test
    void eliminar_deberiaEliminarPublicidadDelTitular() {
        autenticarComoEmpleador();
        when(repository.findById(1L)).thenReturn(Optional.of(entidad));

        service.eliminar(1L);

        verify(repository, times(1)).delete(entidad);
    }

    @Test
    void eliminar_deberiaRechazarSiPublicidadNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(999L));
    }

    @Test
    void obtenerPorAnunciante_deberiaRetornarAnunciosDelAnunciante() {
        autenticarComoEmpleador();
        when(repository.findByAnuncianteId(1L)).thenReturn(List.of(entidad));
        when(mapper.toDTO(entidad)).thenReturn(dto);

        List<PublicidadDTO> resultado = service.obtenerPorAnunciante(1L);

        assertEquals(1, resultado.size());
    }
}
