package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.ComunicadoMapper;
import com.al_lado.al_lado_project.repository.comunidad.ComunicadoRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.ComunicadoService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * HU-20: Leer los comunicados institucionales de la plataforma.
 *
 * <p>Pruebas unitarias de {@link ComunicadoServiceImpl}. Valida listado
 * paginado, filtrado por alcance, creacion restringida a admin y
 * eliminacion.</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ComunicadoServiceImplTest {

    @Mock
    private ComunicadoRepository repository;

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private ComunicadoMapper mapper;

    private ComunicadoService service;

    private Administrador admin;
    private Comunicado comunicado;
    private ComunicadoDTO dto;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new ComunicadoServiceImpl(repository, administradorRepository, mapper);

        admin = new Administrador();
        admin.setId(1L);

        comunicado = new Comunicado();
        comunicado.setId(1L);
        comunicado.setTitulo("Aviso importante");
        comunicado.setContenido("Nuevas funciones disponibles");
        comunicado.setAlcance("GLOBAL");
        comunicado.setEmisor(admin);

        dto = new ComunicadoDTO();
        dto.setId(1L);
        dto.setTitulo("Aviso importante");
        dto.setContenido("Nuevas funciones disponibles");
        dto.setAlcance("GLOBAL");
    }

    private void autenticarComoAdmin() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities()));
    }

    private void autenticarComoTrabajador() {
        Trabajador trabajador = new Trabajador("Trabajador", "trabajador@demo.com", "password");
        trabajador.setId(2L);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(trabajador, null, trabajador.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void obtenerTodos_deberiaRetornarPaginaDeComunicados() {
        autenticarComoAdmin();
        Page<Comunicado> pagina = new PageImpl<>(List.of(comunicado), PageRequest.of(0, 10), 1);
        when(repository.findAllByOrderByFechaEmisionDesc(any(Pageable.class))).thenReturn(pagina);
        when(mapper.toDTO(comunicado)).thenReturn(dto);
        when(mapper.toDTOPublic(comunicado)).thenReturn(dto);

        Page<ComunicadoDTO> resultado = service.obtenerTodos(PageRequest.of(0, 10));

        assertEquals(1, resultado.getContent().size());
        assertEquals("Aviso importante", resultado.getContent().get(0).getTitulo());
    }

    @Test
    void obtenerPorAlcance_deberiaFiltrarPorAlcance() {
        autenticarComoAdmin();
        Page<Comunicado> pagina = new PageImpl<>(List.of(comunicado), PageRequest.of(0, 10), 1);
        when(repository.findByAlcanceIgnoreCase("GLOBAL", PageRequest.of(0, 10))).thenReturn(pagina);
        when(mapper.toDTO(comunicado)).thenReturn(dto);
        when(mapper.toDTOPublic(comunicado)).thenReturn(dto);

        Page<ComunicadoDTO> resultado = service.obtenerPorAlcance("GLOBAL", PageRequest.of(0, 10));

        assertEquals(1, resultado.getContent().size());
        assertEquals("GLOBAL", resultado.getContent().get(0).getAlcance());
    }

    @Test
    void crear_deberiaFijarEmisorDesdeSesionDeAdmin() {
        autenticarComoAdmin();
        when(administradorRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(mapper.toEntity(dto)).thenReturn(comunicado);
        when(repository.save(comunicado)).thenReturn(comunicado);
        when(mapper.toDTO(comunicado)).thenReturn(dto);

        ComunicadoDTO resultado = service.crear(dto);

        assertNotNull(resultado);
        verify(repository, times(1)).save(comunicado);
    }

    @Test
    void crear_deberiaRechazarSiNoEsAdmin() {
        autenticarComoTrabajador();
        dto.setEmisor(new com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO());
        dto.getEmisor().setId(1L);

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> service.crear(dto));
    }

    @Test
    void eliminar_deberiaEliminarComunicadoExistente() {
        when(repository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_deberiaRechazarSiComunicadoNoExiste() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(999L));
    }
}
