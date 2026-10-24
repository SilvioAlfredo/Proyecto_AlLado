package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.ValoracionMapper;
import com.al_lado.al_lado_project.repository.comunidad.ValoracionRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
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
 * RF-05 / HU-06: reglas de valoracion.
 *
 * <p>Cubre el rango de puntuacion (1 a 5), la unicidad de la valoracion por
 * par empleador-trabajador y la autoria: solo el empleador autor o un
 * administrador pueden crear o borrar una valoracion.</p>
 */
@ExtendWith(MockitoExtension.class)
class ValoracionServiceImplTest {

    @Mock
    private ValoracionRepository repository;

    @Mock
    private EmpleadorRepository empleadorRepository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    private final ValoracionMapper mapper = new ValoracionMapper(new com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper());

    private ValoracionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ValoracionServiceImpl(repository, empleadorRepository, trabajadorRepository, mapper);
        // El autor de la valoracion es el empleador 2.
        Empleador auth = new Empleador();
        auth.setId(2L);
        auth.setNombre("Demo Empleador");
        auth.setEmail("empleador@demo.com");
        auth.setPassword("x");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(auth, null, auth.getAuthorities()));
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void crearValoracion_deberiaRechazarPuntuacionCero() {
        // Dado
        ValoracionDTO dto = valoracionDTO(2L, 3L, 0);

        // Cuando / Entonces
        assertThrows(BusinessRuleException.class, () -> service.crear(dto));
        verify(repository, never()).save(any(Valoracion.class));
    }

    @Test
    void crearValoracion_deberiaRechazarPuntuacionSeis() {
        // Dado
        ValoracionDTO dto = valoracionDTO(2L, 3L, 6);

        // Cuando / Entonces
        assertThrows(BusinessRuleException.class, () -> service.crear(dto));
        verify(repository, never()).save(any(Valoracion.class));
    }

    @Test
    void crearValoracion_deberiaRechazarPuntuacionNula() {
        // Dado
        ValoracionDTO dto = valoracionDTO(2L, 3L, null);

        // Cuando / Entonces
        assertThrows(BusinessRuleException.class, () -> service.crear(dto));
        verify(repository, never()).save(any(Valoracion.class));
    }

    @Test
    void crearValoracion_deberiaAceptarPuntuacionMinima() {
        // Dado
        ValoracionDTO dto = valoracionDTO(2L, 3L, 1);
        when(repository.existsByAutorIdAndTrabajadorId(2L, 3L)).thenReturn(false);
        when(empleadorRepository.findById(2L)).thenReturn(Optional.of(empleador(2L)));
        when(trabajadorRepository.findById(3L)).thenReturn(Optional.of(trabajador(3L)));
        when(repository.save(any(Valoracion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        ValoracionDTO resultado = service.crear(dto);

        // Entonces
        assertEquals(1, resultado.getPuntuacion());
        verify(repository, times(1)).save(any(Valoracion.class));
    }

    @Test
    void crearValoracion_deberiaAceptarPuntuacionMaxima() {
        // Dado
        ValoracionDTO dto = valoracionDTO(2L, 3L, 5);
        when(repository.existsByAutorIdAndTrabajadorId(2L, 3L)).thenReturn(false);
        when(empleadorRepository.findById(2L)).thenReturn(Optional.of(empleador(2L)));
        when(trabajadorRepository.findById(3L)).thenReturn(Optional.of(trabajador(3L)));
        when(repository.save(any(Valoracion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        ValoracionDTO resultado = service.crear(dto);

        // Entonces
        assertEquals(5, resultado.getPuntuacion());
    }

    @Test
    void crearValoracion_deberiaRechazarAutorAusente() {
        // Dado
        ValoracionDTO dto = valoracionDTO(null, 3L, 4);

        // Cuando / Entonces
        assertThrows(BusinessRuleException.class, () -> service.crear(dto));
    }

    @Test
    void crearValoracion_deberiaRechazarTrabajadorAusente() {
        // Dado
        ValoracionDTO dto = valoracionDTO(2L, null, 4);

        // Cuando / Entonces
        assertThrows(BusinessRuleException.class, () -> service.crear(dto));
    }

    @Test
    void crearValoracion_deberiaRechazarValoracionDuplicada() {
        // Dado
        ValoracionDTO dto = valoracionDTO(2L, 3L, 4);
        when(repository.existsByAutorIdAndTrabajadorId(2L, 3L)).thenReturn(true);

        // Cuando / Entonces
        assertThrows(DuplicateResourceException.class, () -> service.crear(dto));
        verify(repository, never()).save(any(Valoracion.class));
    }

    @Test
    void crearValoracion_deberiaRechazarAutorQueNoEsElAutenticado() {
        // Dado: el autenticado es el empleador 2 y se quiere valorar como el 9.
        ValoracionDTO dto = valoracionDTO(9L, 3L, 4);

        // Cuando / Entonces
        assertThrows(AccessDeniedException.class, () -> service.crear(dto));
    }

    @Test
    void crearValoracion_deberiaFallarSiElEmpleadorNoExiste() {
        // Dado
        ValoracionDTO dto = valoracionDTO(2L, 3L, 4);
        when(repository.existsByAutorIdAndTrabajadorId(2L, 3L)).thenReturn(false);
        when(empleadorRepository.findById(2L)).thenReturn(Optional.empty());

        // Cuando / Entonces
        assertThrows(ResourceNotFoundException.class, () -> service.crear(dto));
    }

    @Test
    void eliminarValoracion_deberiaFallarSiNoExiste() {
        // Dado
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Cuando / Entonces
        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(99L));
    }

    @Test
    void eliminarValoracion_deberiaBorrarLaDelAutorAutenticado() {
        // Dado
        Valoracion valoracion = new Valoracion();
        valoracion.setId(5L);
        valoracion.setAutor(empleador(2L));
        when(repository.findById(5L)).thenReturn(Optional.of(valoracion));

        // Cuando
        service.eliminar(5L);

        // Entonces
        verify(repository, times(1)).delete(valoracion);
    }

    @Test
    void eliminarValoracion_deberiaRechazarAutorAjeno() {
        // Dado: la valoracion pertenece al empleador 9 y el autenticado es el 2.
        Valoracion valoracion = new Valoracion();
        valoracion.setId(5L);
        valoracion.setAutor(empleador(9L));
        when(repository.findById(5L)).thenReturn(Optional.of(valoracion));

        // Cuando / Entonces
        assertThrows(AccessDeniedException.class, () -> service.eliminar(5L));
        verify(repository, never()).delete(any(Valoracion.class));
    }

    @Test
    void obtenerPromedio_deberiaDevolverCeroCuandoNoHayValoraciones() {
        // Dado
        when(repository.calcularPromedioPuntuacionPorTrabajadorId(3L)).thenReturn(null);

        // Cuando
        Double promedio = service.obtenerPromedioPorTrabajador(3L);

        // Entonces
        assertEquals(0.0, promedio);
    }

    private ValoracionDTO valoracionDTO(Long autorId, Long trabajadorId, Integer puntuacion) {
        ValoracionDTO dto = new ValoracionDTO();
        if (autorId != null) {
            UsuarioResumenDTO autor = new UsuarioResumenDTO();
            autor.setId(autorId);
            dto.setAutor(autor);
        }
        if (trabajadorId != null) {
            UsuarioResumenDTO trabajador = new UsuarioResumenDTO();
            trabajador.setId(trabajadorId);
            dto.setTrabajador(trabajador);
        }
        dto.setPuntuacion(puntuacion);
        dto.setComentario("Trabajo correcto y puntual");
        return dto;
    }

    private Empleador empleador(Long id) {
        Empleador empleador = new Empleador();
        empleador.setId(id);
        empleador.setNombre("Empleador");
        empleador.setEmail("empleador" + id + "@demo.com");
        empleador.setPassword("x");
        return empleador;
    }

    private Trabajador trabajador(Long id) {
        Trabajador trabajador = new Trabajador();
        trabajador.setId(id);
        trabajador.setNombre("Trabajador");
        trabajador.setEmail("trabajador" + id + "@demo.com");
        trabajador.setPassword("x");
        return trabajador;
    }
}
