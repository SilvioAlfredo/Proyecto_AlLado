package com.al_lado.al_lado_project.service.perfil.impl;

import com.al_lado.al_lado_project.dto.perfil.PortafolioItemDTO;
import com.al_lado.al_lado_project.entity.perfil.PortafolioItem;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.perfil.PortafolioItemMapper;
import com.al_lado.al_lado_project.repository.perfil.PortafolioItemRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.perfil.PortafolioItemService;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * HU-17: Armar mi portafolio y acceder a propuestas reservadas para usuarios PRO.
 *
 * <p>Pruebas unitarias de {@link PortafolioItemServiceImpl}. Valida agregar y
 * eliminar items de portafolio, requisito de PRO vigente y titularidad.</p>
 */
@ExtendWith(MockitoExtension.class)
class PortafolioItemServiceImplTest {

    @Mock
    private PortafolioItemRepository repository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private PortafolioItemMapper mapper;

    @Mock
    private SuscripcionService suscripcionService;

    private PortafolioItemService service;

    private Trabajador trabajador;
    private PortafolioItemDTO dto;
    private PortafolioItem entidad;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new PortafolioItemServiceImpl(repository, trabajadorRepository, mapper, suscripcionService);

        trabajador = new Trabajador("Trabajador Demo", "trabajador@demo.com", "password");
        trabajador.setId(1L);

        dto = new PortafolioItemDTO();
        dto.setId(1L);
        dto.setTitulo("Proyecto de construccion");
        dto.setDescripcion("Construccion de vivienda familiar");
        dto.setUrlArchivo("https://comercio.com/proyecto.pdf");

        entidad = new PortafolioItem();
        entidad.setId(1L);
        entidad.setTitulo("Proyecto de construccion");
    }

    private void autenticarComoTrabajador() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(trabajador, null, trabajador.getAuthorities()));
    }

    private void autenticarComoOtro() {
        Trabajador otro = new Trabajador("Otro", "otro@demo.com", "password");
        otro.setId(99L);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(otro, null, otro.getAuthorities()));
    }

    @Test
    void obtenerPorTrabajador_deberiaRetornarItemsDelTrabajador() {
        when(repository.findByTrabajadorId(1L)).thenReturn(List.of(entidad));
        when(mapper.toDTO(entidad)).thenReturn(dto);

        List<PortafolioItemDTO> resultado = service.obtenerPorTrabajador(1L);

        assertEquals(1, resultado.size());
        assertEquals("Proyecto de construccion", resultado.get(0).getTitulo());
    }

    @Test
    void agregar_deberiaCrearItemConPROVigente() {
        autenticarComoTrabajador();
        when(suscripcionService.esProVigente(1L)).thenReturn(true);
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(mapper.toEntity(dto)).thenReturn(entidad);
        when(repository.save(entidad)).thenReturn(entidad);
        when(mapper.toDTO(entidad)).thenReturn(dto);

        PortafolioItemDTO resultado = service.agregar(1L, dto);

        assertNotNull(resultado);
        assertEquals("Proyecto de construccion", resultado.getTitulo());
        verify(repository, times(1)).save(entidad);
    }

    @Test
    void agregar_deberiaRechazarSiNoTienePROVigente() {
        autenticarComoTrabajador();
        when(suscripcionService.esProVigente(1L)).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> service.agregar(1L, dto));
        verify(repository, never()).save(any());
    }

    @Test
    void agregar_deberiaRechazarSiTrabajadorNoExiste() {
        autenticarComoTrabajador();
        when(suscripcionService.esProVigente(1L)).thenReturn(true);
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.agregar(1L, dto));
    }

    @Test
    void agregar_deberiaRechazarAccesoAjeno() {
        autenticarComoOtro();

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.agregar(1L, dto));
    }

    @Test
    void eliminar_deberiaEliminarItemDelTitular() {
        entidad.setTrabajador(trabajador);
        autenticarComoTrabajador();
        when(repository.findById(1L)).thenReturn(Optional.of(entidad));

        service.eliminar(1L);

        verify(repository, times(1)).delete(entidad);
    }

    @Test
    void eliminar_deberiaRechazarSiItemNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(999L));
    }

    @Test
    void eliminar_deberiaRechazarAccesoAjeno() {
        Trabajador otro = new Trabajador("Otro", "otro@demo.com", "password");
        otro.setId(99L);
        entidad.setTrabajador(otro);
        autenticarComoTrabajador();
        when(repository.findById(1L)).thenReturn(Optional.of(entidad));

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> service.eliminar(1L));
    }
}
