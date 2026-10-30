package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.usuario.EstadisticasTrabajadorDTO;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionMapper;
import com.al_lado.al_lado_project.repository.comunidad.ValoracionRepository;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.service.comunidad.NotificacionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadisticasTrabajadorTest {

    @Mock private PostulacionRepository postulacionRepository;
    @Mock private TrabajadorRepository trabajadorRepository;
    @Mock private OportunidadLaboralRepository oportunidadRepository;
    @Mock private ValoracionRepository valoracionRepository;
    @Mock private PostulacionMapper mapper;
    @Mock private NotificacionService notificacionService;

    private PostulacionServiceImpl service;
    private Trabajador trabajador;

    @BeforeEach
    void setUp() {
        service = new PostulacionServiceImpl(postulacionRepository, trabajadorRepository,
                oportunidadRepository, valoracionRepository, mapper, notificacionService);
        trabajador = new Trabajador();
        trabajador.setId(42L);
        trabajador.setNombre("Julio");
        trabajador.setEmail("julio@example.test");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(trabajador, null, trabajador.getAuthorities()));
        when(postulacionRepository.countByAutorIdAndEstadoIgnoreCase(42L, "COMPLETADA")).thenReturn(2L);
        when(postulacionRepository.countByAutorIdAndEstadoIgnoreCase(42L, "SELECCIONADA")).thenReturn(1L);
        when(postulacionRepository.countByAutorId(42L)).thenReturn(5L);
        when(valoracionRepository.countByTrabajadorId(42L)).thenReturn(3L);
        when(valoracionRepository.calcularPromedioPuntuacionPorTrabajadorId(42L)).thenReturn(4.5);
        when(trabajadorRepository.findById(42L)).thenReturn(Optional.of(trabajador));
    }

    @AfterEach
    void limpiarSeguridad() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void proVigenteRecibeSumasDePostulacionesCompletadas() {
        trabajador.setSuscripcion(new Suscripcion(trabajador, "PRO", LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(10), true));
        when(postulacionRepository.sumarImportesCompletadosPorTrabajador(42L)).thenReturn(125000.0);
        when(postulacionRepository.sumarImportesCompletadosPorTrabajadorDesde(any(), any())).thenReturn(45000.0);

        EstadisticasTrabajadorDTO stats = service.obtenerEstadisticasTrabajador(42L);

        assertTrue(stats.isIngresosDisponibles());
        assertEquals(125000.0, stats.getIngresosTotales().doubleValue());
        assertEquals(45000.0, stats.getIngresosUltimos3Meses().doubleValue());
        assertEquals(2, stats.getPostulacionesCompletadas());
        assertEquals(5, stats.getPostulacionesEnviadas());
    }

    @Test
    void planBasicoVeCandadoYNoRecibeImportes() {
        trabajador.setSuscripcion(new Suscripcion(trabajador, "BASICO", LocalDate.now().minusDays(1), null, true));

        EstadisticasTrabajadorDTO stats = service.obtenerEstadisticasTrabajador(42L);

        assertFalse(stats.isIngresosDisponibles());
        assertEquals(0.0, stats.getIngresosTotales().doubleValue());
        assertEquals(0.0, stats.getIngresosUltimos3Meses().doubleValue());
        verify(postulacionRepository, never()).sumarImportesCompletadosPorTrabajador(any());
        verify(postulacionRepository, never()).sumarImportesCompletadosPorTrabajadorDesde(any(), any());
    }
}
