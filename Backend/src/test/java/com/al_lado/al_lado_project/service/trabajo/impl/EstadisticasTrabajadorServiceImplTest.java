package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.usuario.EstadisticasTrabajadorDTO;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.repository.comunidad.ValoracionRepository;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.usuario.SuscripcionVigencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * HU-12: Visualizar estadisticas de ingresos estimados y trabajos realizados.
 *
 * <p>Pruebas unitarias del metodo {@code construirEstadisticas} de
 * {@link PostulacionServiceImpl}. Valida calculo de nivel de recomendacion a
 * partir del promedio real de valoraciones, ingresos por PRO vigente, y
 * conteos de postulaciones por estado.</p>
 */
@ExtendWith(MockitoExtension.class)
class EstadisticasTrabajadorServiceImplTest {

    @Mock
    private PostulacionRepository repository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private ValoracionRepository valoracionRepository;

    private PostulacionServiceImpl service;

    private Trabajador trabajador;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new PostulacionServiceImpl(
                repository, trabajadorRepository, null, valoracionRepository, null, null);

        trabajador = new Trabajador("Trabajador Demo", "trabajador@demo.com", "password");
        trabajador.setId(1L);
        trabajador.setFotografia("https://ejemplo.com/foto.jpg");
    }

    private void autenticarComoTrabajador() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(trabajador, null, trabajador.getAuthorities()));
    }

    @Test
    void construirEstadisticas_deberiaCalcularNivelRecomendacionDesdePromedioReal() {
        autenticarComoTrabajador();
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(repository.countByAutorId(1L)).thenReturn(10L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_COMPLETADA)).thenReturn(5L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_SELECCIONADA)).thenReturn(3L);
        when(valoracionRepository.countByTrabajadorId(1L)).thenReturn(4L);
        when(valoracionRepository.calcularPromedioPuntuacionPorTrabajadorId(1L)).thenReturn(4.5);

        EstadisticasTrabajadorDTO stats = service.obtenerEstadisticasTrabajador(1L);

        // promedio.intValue() truncates 4.5 to 4
        assertEquals(4, stats.getNivelRecomendacion());
        assertEquals(BigDecimal.valueOf(4.5), stats.getPromedioValoraciones());
        assertEquals(4, stats.getValoracionesCount());
    }

    @Test
    void construirEstadisticas_deberiaRetornarNivelNuloSinValoraciones() {
        autenticarComoTrabajador();
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(repository.countByAutorId(1L)).thenReturn(0L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_COMPLETADA)).thenReturn(0L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_SELECCIONADA)).thenReturn(0L);
        when(valoracionRepository.countByTrabajadorId(1L)).thenReturn(0L);
        when(valoracionRepository.calcularPromedioPuntuacionPorTrabajadorId(1L)).thenReturn(null);

        EstadisticasTrabajadorDTO stats = service.obtenerEstadisticasTrabajador(1L);

        assertNull(stats.getNivelRecomendacion());
        assertEquals(BigDecimal.ZERO, stats.getPromedioValoraciones());
        assertEquals(0, stats.getValoracionesCount());
    }

    @Test
    void construirEstadisticas_deberiaCalcularNivelMinimo1SiHayValoraciones() {
        autenticarComoTrabajador();
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(repository.countByAutorId(1L)).thenReturn(1L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_COMPLETADA)).thenReturn(1L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_SELECCIONADA)).thenReturn(0L);
        when(valoracionRepository.countByTrabajadorId(1L)).thenReturn(1L);
        when(valoracionRepository.calcularPromedioPuntuacionPorTrabajadorId(1L)).thenReturn(1.0);

        EstadisticasTrabajadorDTO stats = service.obtenerEstadisticasTrabajador(1L);

        assertEquals(1, stats.getNivelRecomendacion());
    }

    @Test
    void construirEstadisticas_deberiaCalcularNivelMaximo5SiPromedioEs5() {
        autenticarComoTrabajador();
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(repository.countByAutorId(1L)).thenReturn(1L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_COMPLETADA)).thenReturn(1L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_SELECCIONADA)).thenReturn(0L);
        when(valoracionRepository.countByTrabajadorId(1L)).thenReturn(1L);
        when(valoracionRepository.calcularPromedioPuntuacionPorTrabajadorId(1L)).thenReturn(5.0);

        EstadisticasTrabajadorDTO stats = service.obtenerEstadisticasTrabajador(1L);

        assertEquals(5, stats.getNivelRecomendacion());
    }

    @Test
    void construirEstadisticas_deberiaEntregarIngresosSoloConPROVigente() {
        autenticarComoTrabajador();
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(repository.countByAutorId(1L)).thenReturn(5L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_COMPLETADA)).thenReturn(3L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_SELECCIONADA)).thenReturn(1L);
        when(valoracionRepository.countByTrabajadorId(1L)).thenReturn(2L);
        when(valoracionRepository.calcularPromedioPuntuacionPorTrabajadorId(1L)).thenReturn(4.0);
        Suscripcion pro = new Suscripcion();
        pro.setNivel("PRO");
        pro.setActiva(true);
        pro.setFechaInicio(LocalDate.of(2026, 1, 1));
        pro.setFechaFin(LocalDate.of(2027, 1, 1));
        trabajador.setSuscripcion(pro);
        when(repository.sumarImportesCompletadosPorTrabajador(1L)).thenReturn(50000.0);
        when(repository.sumarImportesCompletadosPorTrabajadorDesde(
                anyLong(), any(java.time.LocalDateTime.class))).thenReturn(30000.0);

        EstadisticasTrabajadorDTO stats = service.obtenerEstadisticasTrabajador(1L);

        assertTrue(stats.isIngresosDisponibles());
        assertEquals(new BigDecimal("50000.0"), stats.getIngresosTotales());
        assertEquals(new BigDecimal("30000.0"), stats.getIngresosUltimos3Meses());
    }

    @Test
    void construirEstadisticas_deberiaOcultarIngresosSinPROVigente() {
        autenticarComoTrabajador();
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(repository.countByAutorId(1L)).thenReturn(5L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_COMPLETADA)).thenReturn(3L);
        when(repository.countByAutorIdAndEstadoIgnoreCase(1L, Postulacion.ESTADO_SELECCIONADA)).thenReturn(1L);
        when(valoracionRepository.countByTrabajadorId(1L)).thenReturn(2L);
        when(valoracionRepository.calcularPromedioPuntuacionPorTrabajadorId(1L)).thenReturn(4.0);
        Suscripcion basico = new Suscripcion();
        basico.setNivel("BASICO");
        basico.setActiva(true);
        basico.setFechaInicio(LocalDate.of(2026, 1, 1));
        basico.setFechaFin(null);
        trabajador.setSuscripcion(basico);

        EstadisticasTrabajadorDTO stats = service.obtenerEstadisticasTrabajador(1L);

        assertFalse(stats.isIngresosDisponibles());
        assertEquals(BigDecimal.ZERO, stats.getIngresosTotales());
        assertEquals(BigDecimal.ZERO, stats.getIngresosUltimos3Meses());
    }
}
