package com.al_lado.al_lado_project.service.comunidad.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.al_lado.al_lado_project.dto.comunidad.FavoritoDTO;
import com.al_lado.al_lado_project.entity.comunidad.Favorito;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.mappers.comunidad.FavoritoMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import com.al_lado.al_lado_project.repository.comunidad.FavoritoRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.service.comunidad.FavoritoService;

@ExtendWith(MockitoExtension.class)
class FavoritoServiceImplTest {

    @Mock private FavoritoRepository favoritoRepository;
    @Mock private EmpleadorRepository empleadorRepository;
    @Mock private TrabajadorRepository trabajadorRepository;
    private final FavoritoMapper mapper = new FavoritoMapper(new UsuarioResumenMapper());
    private FavoritoService service;

    private Empleador empleador;
    private Trabajador trabajador;
    private Favorito favorito;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new FavoritoServiceImpl(favoritoRepository, empleadorRepository, trabajadorRepository, mapper);

        empleador = new Empleador();
        empleador.setId(1L);
        empleador.setNombre("Empleador Test");
        empleador.setEmail("emp@test.com");
        empleador.setPassword("x");
        // La sesion es del empleador titular de la lista de favoritos.
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(empleador, null, empleador.getAuthorities()));

        trabajador = new Trabajador();
        trabajador.setId(2L);
        trabajador.setNombre("Trabajador Test");
        trabajador.setEmail("tra@test.com");
        trabajador.setPassword("x");

        favorito = new Favorito();
        favorito.setId(1L);
        favorito.setEmpleador(empleador);
        favorito.setTrabajador(trabajador);
    }

    @AfterEach
    void limpiarContexto() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void agregarFavorito_deberiaCrearFavoritoCuandoDatosSonValidos() {
        // Dado
        when(favoritoRepository.existsByEmpleadorIdAndTrabajadorId(1L, 2L)).thenReturn(false);
        when(empleadorRepository.findById(1L)).thenReturn(Optional.of(empleador));
        when(trabajadorRepository.findById(2L)).thenReturn(Optional.of(trabajador));
        when(favoritoRepository.save(any(Favorito.class))).thenReturn(favorito);

        // Cuando
        FavoritoDTO resultado = service.agregar(1L, 2L);

        // Entonces
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(favoritoRepository, times(1)).save(any(Favorito.class));
    }

    @Test
    void agregarFavorito_deberiaRechazarCuandoYaExiste() {
        // Dado
        when(favoritoRepository.existsByEmpleadorIdAndTrabajadorId(1L, 2L)).thenReturn(true);

        // Cuando
        assertThrows(DuplicateResourceException.class, () -> service.agregar(1L, 2L));

        // Entonces
        verify(favoritoRepository, never()).save(any(Favorito.class));
    }

    @Test
    void quitarFavorito_deberiaEliminarCuandoExiste() {
        // Dado
        when(favoritoRepository.existsByEmpleadorIdAndTrabajadorId(1L, 2L)).thenReturn(true);

        // Cuando
        service.quitar(1L, 2L);

        // Entonces
        verify(favoritoRepository, times(1)).deleteByEmpleadorIdAndTrabajadorId(1L, 2L);
    }

    @Test
    void obtenerPorEmpleador_deberiaListarFavoritos() {
        // Dado
        when(favoritoRepository.findByEmpleadorId(1L)).thenReturn(List.of(favorito));

        // Cuando
        var resultado = service.obtenerPorEmpleador(1L);

        // Entonces
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }
}
