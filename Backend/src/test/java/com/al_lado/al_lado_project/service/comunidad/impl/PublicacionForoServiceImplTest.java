package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.mappers.comunidad.PublicacionForoMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import com.al_lado.al_lado_project.repository.comunidad.PublicacionForoRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RF-13 / HU-13 CA-02: reglas del foro de la comunidad.
 *
 * <p><b>DEFECTO ABIERTO (TC-SEC-002).</b> El requerimiento pide que el foro solo
 * muestre publicaciones del mismo barrio, pero
 * {@code PublicacionForoServiceImpl.obtenerTodas} devuelve todas las
 * publicaciones sin filtrar por sector. El caso
 * {@code obtenerTodas_noDeberiaFiltrarPorBarrio} deja constancia de esa
 * ausencia de filtro.</p>
 */
@ExtendWith(MockitoExtension.class)
class PublicacionForoServiceImplTest {

    @Mock
    private PublicacionForoRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private final PublicacionForoMapper mapper = new PublicacionForoMapper(new UsuarioResumenMapper());

    private PublicacionForoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PublicacionForoServiceImpl(repository, usuarioRepository, mapper);

        Trabajador auth = new Trabajador();
        auth.setId(3L);
        auth.setNombre("Demo Trabajador");
        auth.setEmail("trabajador@demo.com");
        auth.setPassword("x");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(auth, null, auth.getAuthorities()));
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void crearPublicacion_deberiaRechazarAutorAusente() {
        // Dado
        PublicacionForoDTO dto = new PublicacionForoDTO();
        dto.setTitulo("Publicacion sin autor");
        dto.setContenido("Contenido");

        // Cuando / Entonces
        assertThrows(BusinessRuleException.class, () -> service.crear(dto));
        verify(repository, never()).save(any(PublicacionForo.class));
    }

    @Test
    void crearPublicacion_deberiaRechazarAutorAjeno() {
        // Dado: la sesion es del trabajador 3 y se publica como el 9.
        PublicacionForoDTO dto = publicacionDTO(9L);

        // Cuando / Entonces
        assertThrows(AccessDeniedException.class, () -> service.crear(dto));
        verify(repository, never()).save(any(PublicacionForo.class));
    }

    @Test
    void crearPublicacion_deberiaGuardarYDevolverElAutor() {
        // Dado
        PublicacionForoDTO dto = publicacionDTO(3L);
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(trabajador(3L)));
        when(repository.save(any(PublicacionForo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        PublicacionForoDTO resultado = service.crear(dto);

        // Entonces
        assertEquals("Publicacion de prueba", resultado.getTitulo());
        assertEquals(3L, resultado.getAutor().getId());
        verify(repository, times(1)).save(any(PublicacionForo.class));
    }

    @Test
    void eliminarPublicacion_deberiaRechazarPublicacionAjena() {
        // Dado
        PublicacionForo publicacion = new PublicacionForo();
        publicacion.setId(1L);
        publicacion.setAutor(trabajador(9L));
        when(repository.findById(1L)).thenReturn(Optional.of(publicacion));

        // Cuando / Entonces
        assertThrows(AccessDeniedException.class, () -> service.eliminar(1L));
        verify(repository, never()).delete(any(PublicacionForo.class));
    }

    @Test
    void eliminarPublicacion_deberiaBorrarLaPropia() {
        // Dado
        PublicacionForo publicacion = new PublicacionForo();
        publicacion.setId(1L);
        publicacion.setAutor(trabajador(3L));
        when(repository.findById(1L)).thenReturn(Optional.of(publicacion));

        // Cuando
        service.eliminar(1L);

        // Entonces
        verify(repository, times(1)).delete(publicacion);
    }

    @Test
    void obtenerTodas_noDeberiaFiltrarPorBarrio() {
        // Dado: el listado no recibe ningun criterio de barrio ni de sector.
        when(repository.findAllByOrderByFechaCreacionDesc(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of()));

        // Cuando
        var resultado = service.obtenerTodas(org.springframework.data.domain.Pageable.unpaged());

        // Entonces: hoy el listado no aplica ningun filtro por barrio (defecto TC-SEC-002).
        assertEquals(0, resultado.getTotalElements());
        verify(repository, times(1))
                .findAllByOrderByFechaCreacionDesc(any(org.springframework.data.domain.Pageable.class));
    }

    private PublicacionForoDTO publicacionDTO(Long autorId) {
        PublicacionForoDTO dto = new PublicacionForoDTO();
        dto.setTitulo("Publicacion de prueba");
        dto.setContenido("Contenido de la publicacion");
        UsuarioResumenDTO autor = new UsuarioResumenDTO();
        autor.setId(autorId);
        dto.setAutor(autor);
        return dto;
    }

    private Trabajador trabajador(Long id) {
        Trabajador trabajador = new Trabajador();
        trabajador.setId(id);
        trabajador.setNombre("Trabajador " + id);
        trabajador.setEmail("trabajador" + id + "@demo.com");
        trabajador.setPassword("x");
        trabajador.setSector("Centro");
        return trabajador;
    }
}
