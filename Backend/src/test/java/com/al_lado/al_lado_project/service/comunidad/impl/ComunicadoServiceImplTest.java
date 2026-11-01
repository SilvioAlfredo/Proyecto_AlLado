package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.ComunicadoMapper;
import com.al_lado.al_lado_project.repository.comunidad.ComunicadoRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.ComunicadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
class ComunicadoServiceImplTest {

    @Mock
    private ComunicadoRepository repository;

    @Mock
    private ComunicadoMapper mapper;

    private ComunicadoService service;

    private Administrador admin;
    private Comunicado comunicado;
    private ComunicadoDTO dto;

    @BeforeEach
    void setUp() {
        service = new ComunicadoServiceImpl(repository, mapper);

        admin = new Administrador();
        admin.setId(1L);

        comunicado = new Comunicado();
        comunicado.setId(1L);
        comunicado.setTitulo("Aviso importante");
        comunicado.setContenido("Nuevas funciones disponibles");
        comunicado.setAlcance("GLOBAL");

        dto = new ComunicadoDTO();
        dto.setId(1L);
        dto.setTitulo("Aviso importante");
        dto.setContenido("Nuevas funciones disponibles");
        dto.setAlcance("GLOBAL");
    }

    @Test
    void obtenerTodos_deberiaRetornarPaginaDeComunicados() {
        // Dado: existen comunicados publicados
        Page<Comunicado> pagina = new PageImpl<>(List.of(comunicado), PageRequest.of(0, 10), 1);
        when(repository.findAll(any(Pageable.class))).thenReturn(pagina);
        when(mapper.toDTO(comunicado)).thenReturn(dto);

        // Cuando
        Page<ComunicadoDTO> resultado = service.obtenerTodos(PageRequest.of(0, 10));

        // Entonces
        assertEquals(1, resultado.getContent().size());
        assertEquals("Aviso importante", resultado.getContent().get(0).getTitulo());
    }

    @Test
    void obtenerPorAlcance_deberiaFiltrarPorAlcance() {
        // Dado: existen comunicados con alcance GLOBAL
        Page<Comunicado> pagina = new PageImpl<>(List.of(comunicado), PageRequest.of(0, 10), 1);
        when(repository.findByAlcanceIgnoreCase("GLOBAL", PageRequest.of(0, 10))).thenReturn(pagina);
        when(mapper.toDTO(comunicado)).thenReturn(dto);

        // Cuando
        Page<ComunicadoDTO> resultado = service.obtenerPorAlcance("GLOBAL", PageRequest.of(0, 10));

        // Entonces
        assertEquals(1, resultado.getContent().size());
        assertEquals("GLOBAL", resultado.getContent().get(0).getAlcance());
    }

    @Test
    void crear_deberiaFijarEmisorDesdeSesionDeAdmin() {
        // Dado: el admin autenticado crea un comunicado
        when(SecurityUtils.isAdmin()).thenReturn(true);
        when(SecurityUtils.currentUserId()).thenReturn(1L);
        when(mapper.toEntity(dto)).thenReturn(comunicado);
        when(repository.save(comunicado)).thenReturn(comunicado);
        when(mapper.toDTO(comunicado)).thenReturn(dto);

        // Cuando
        ComunicadoDTO resultado = service.crear(dto);

        // Entonces
        assertNotNull(resultado);
        verify(repository, times(1)).save(comunicado);
    }

    @Test
    void crear_deberiaRechazarSiNoEsAdmin() {
        // Dado: el autenticado no es administrador
        when(SecurityUtils.isAdmin()).thenReturn(false);

        // Cuando / Entonces
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> service.crear(dto));
    }

    @Test
    void eliminar_deberiaEliminarComunicadoExistente() {
        // Dado: el comunicado existe
        when(repository.existsById(1L)).thenReturn(true);

        // Cuando
        service.eliminar(1L);

        // Entonces
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_deberiaRechazarSiComunicadoNoExiste() {
        // Dado: el id de comunicado no existe
        when(repository.existsById(999L)).thenReturn(false);

        // Cuando / Entonces
        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(999L));
    }
}
