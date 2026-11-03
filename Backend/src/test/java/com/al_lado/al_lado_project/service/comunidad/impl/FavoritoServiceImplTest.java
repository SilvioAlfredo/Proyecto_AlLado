package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.FavoritoDTO;
import com.al_lado.al_lado_project.entity.comunidad.Favorito;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.FavoritoMapper;
import com.al_lado.al_lado_project.repository.comunidad.FavoritoRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.service.comunidad.FavoritoService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * HU-11: Empleador almacena favoritos de trabajadores.
 *
 * <p>Pruebas unitarias de {@link FavoritoServiceImpl}. Valida agregar, quitar
 * y listar favoritos, control de duplicados y titularidad.</p>
 */
@ExtendWith(MockitoExtension.class)
class FavoritoServiceImplTest {

    @Mock
    private FavoritoRepository repository;

    @Mock
    private EmpleadorRepository empleadorRepository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private FavoritoMapper mapper;

    private FavoritoService service;

    private Empleador empleador;
    private Trabajador trabajador;
    private Favorito favorito;
    private FavoritoDTO favoritoDTO;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new FavoritoServiceImpl(repository, empleadorRepository, trabajadorRepository, mapper);

        empleador = new Empleador("Empleador Demo", "empleador@demo.com", "password");
        empleador.setId(1L);

        trabajador = new Trabajador("Trabajador Demo", "trabajador@demo.com", "password");
        trabajador.setId(2L);

        favorito = new Favorito(empleador, trabajador);
        favorito.setId(10L);

        favoritoDTO = new FavoritoDTO();
        favoritoDTO.setId(10L);
    }

    private void autenticarComoEmpleador() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(empleador, null, empleador.getAuthorities()));
    }

    private void autenticarComoOtro() {
        Empleador otro = new Empleador("Otro", "otro@demo.com", "password");
        otro.setId(99L);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(otro, null, otro.getAuthorities()));
    }

    @Test
    void obtenerPorEmpleador_deberiaRetornarFavoritosDelEmpleador() {
        autenticarComoEmpleador();
        when(repository.findByEmpleadorId(1L)).thenReturn(List.of(favorito));
        when(mapper.toDTO(favorito)).thenReturn(favoritoDTO);

        List<FavoritoDTO> resultado = service.obtenerPorEmpleador(1L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getId());
        verify(repository, times(1)).findByEmpleadorId(1L);
    }

    @Test
    void obtenerPorEmpleador_deberiaRechazarAccesoAjeno() {
        autenticarComoOtro();

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.obtenerPorEmpleador(1L));
        verify(repository, never()).findByEmpleadorId(anyLong());
    }

    @Test
    void agregar_deberiaCrearFavoritoCuandoNoExiste() {
        autenticarComoEmpleador();
        when(repository.existsByEmpleadorIdAndTrabajadorId(1L, 2L)).thenReturn(false);
        when(empleadorRepository.findById(1L)).thenReturn(Optional.of(empleador));
        when(trabajadorRepository.findById(2L)).thenReturn(Optional.of(trabajador));
        when(repository.save(any(Favorito.class))).thenReturn(favorito);
        when(mapper.toDTO(favorito)).thenReturn(favoritoDTO);

        FavoritoDTO resultado = service.agregar(1L, 2L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        verify(repository, times(1)).save(any(Favorito.class));
    }

    @Test
    void agregar_deberiaRechadarSiYaEsFavorito() {
        autenticarComoEmpleador();
        when(repository.existsByEmpleadorIdAndTrabajadorId(1L, 2L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.agregar(1L, 2L));
        verify(repository, never()).save(any());
    }

    @Test
    void agregar_deberiaRechazarSiEmpleadorNoExiste() {
        autenticarComoEmpleador();
        when(repository.existsByEmpleadorIdAndTrabajadorId(1L, 2L)).thenReturn(false);
        when(empleadorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.agregar(1L, 2L));
    }

    @Test
    void agregar_deberiaRechazarSiTrabajadorNoExiste() {
        autenticarComoEmpleador();
        when(repository.existsByEmpleadorIdAndTrabajadorId(1L, 2L)).thenReturn(false);
        when(empleadorRepository.findById(1L)).thenReturn(Optional.of(empleador));
        when(trabajadorRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.agregar(1L, 2L));
    }

    @Test
    void quitar_deberiaEliminarFavoritoExistente() {
        autenticarComoEmpleador();
        when(repository.existsByEmpleadorIdAndTrabajadorId(1L, 2L)).thenReturn(true);

        service.quitar(1L, 2L);

        verify(repository, times(1)).deleteByEmpleadorIdAndTrabajadorId(1L, 2L);
    }

    @Test
    void quitar_deberiaRechazarSiFavoritoNoExiste() {
        autenticarComoEmpleador();
        when(repository.existsByEmpleadorIdAndTrabajadorId(1L, 2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.quitar(1L, 2L));
    }

    @Test
    void quitar_deberiaRechazarAccesoAjeno() {
        autenticarComoOtro();

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.quitar(1L, 2L));
    }
}
