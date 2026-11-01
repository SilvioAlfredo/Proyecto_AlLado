package com.al_lado.al_lado_project.service.admin;

import com.al_lado.al_lado_project.dto.admin.DashboardMetricsDTO;
import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import com.al_lado.al_lado_project.entity.comunidad.Donacion;
import com.al_lado.al_lado_project.entity.comunidad.Publicidad;
import com.al_lado.al_lado_project.entity.comunidad.Reporte;
import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.repository.comunidad.ComunicadoRepository;
import com.al_lado.al_lado_project.repository.comunidad.DonacionRepository;
import com.al_lado.al_lado_project.repository.comunidad.PublicidadRepository;
import com.al_lado.al_lado_project.repository.comunidad.ReporteRepository;
import com.al_lado.al_lado_project.repository.comunidad.ValoracionRepository;
import com.al_lado.al_lado_project.repository.comunidad.PublicacionForoRepository;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.trabajo.OportunidadLaboralService;
import com.al_lado.al_lado_project.mappers.comunidad.ComunicadoMapper;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionMapper;
import com.al_lado.al_lado_project.mappers.comunidad.ReporteMapper;
import com.al_lado.al_lado_project.mappers.comunidad.PublicacionForoMapper;
import com.al_lado.al_lado_project.mappers.comunidad.ValoracionMapper;
import com.al_lado.al_lado_project.mappers.comunidad.DonacionMapper;
import com.al_lado.al_lado_project.mappers.usuario.SuscripcionMapper;
import com.al_lado.al_lado_project.mappers.comunidad.PublicidadMapper;
import com.al_lado.al_lado_project.mappers.comunidad.PerfilEmpresarialMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * HU-18 y HU-19: Administrador modera reportes y consulta panel de metricas.
 *
 * <p>Pruebas unitarias de {@link AdminGestionService}. Valida consultas
 * paginadas con filtros para el panel de administracion.</p>
 */
@ExtendWith(MockitoExtension.class)
class AdminGestionServiceTest {

    @Mock
    private ComunicadoRepository comunicadoRepository;

    @Mock
    private PostulacionRepository postulacionRepository;

    @Mock
    private OportunidadLaboralRepository oportunidadLaboralRepository;

    @Mock
    private ReporteRepository reportes;

    @Mock
    private PublicacionForoRepository foro;

    @Mock
    private ValoracionRepository valoraciones;

    @Mock
    private TrabajadorRepository trabajadores;

    @Mock
    private DonacionRepository donaciones;

    @Mock
    private SuscripcionRepository suscripciones;

    @Mock
    private PublicidadRepository publicidades;

    @Mock
    private EmpleadorRepository empresas;

    @Mock
    private Clock clock;

    @Mock
    private OportunidadLaboralService oportunidades;

    @Mock
    private ComunicadoMapper comunicadoMapper;

    @Mock
    private PostulacionMapper postulacionMapper;

    @Mock
    private ReporteMapper reporteMapper;

    @Mock
    private PublicacionForoMapper foroMapper;

    @Mock
    private ValoracionMapper valoracionMapper;

    @Mock
    private DonacionMapper donacionMapper;

    @Mock
    private SuscripcionMapper suscripcionMapper;

    @Mock
    private PublicidadMapper publicidadMapper;

    @Mock
    private PerfilEmpresarialMapper empresaMapper;

    private AdminGestionService gestion;

    @BeforeEach
    void setUp() {
        gestion = new AdminGestionService(
                comunicadoRepository, comunicadoMapper,
                postulacionRepository, postulacionMapper, oportunidades,
                reportes, reporteMapper, foro, foroMapper, valoraciones, valoracionMapper, trabajadores,
                donaciones, donacionMapper, suscripciones, suscripcionMapper, clock,
                publicidades, publicidadMapper, empresas, empresaMapper);
    }

    @Test
    void publicidades_deberiaFiltrarPorEstadoVigentes() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(publicidades.findAll(any(), eq(pageable))).thenReturn(new PageImpl<>(List.of(), pageable));

        var resultado = gestion.publicidades("", "VIGENTES", pageable);

        assertNotNull(resultado);
        verify(publicidades, times(1)).findAll(any(), eq(pageable));
    }

    @Test
    void empresas_deberiaFiltrarPorEstadoVerificadas() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(empresas.findAll(any(), eq(pageable))).thenReturn(new PageImpl<>(List.of(), pageable));

        var resultado = gestion.empresas("", "VERIFICADAS", pageable);

        assertNotNull(resultado);
    }

    @Test
    void reportes_deberiaFiltrarPorEstadoPendiente() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(reportes.findAll(any(), eq(pageable))).thenReturn(new PageImpl<>(List.of(), pageable));

        var resultado = gestion.reportes("", "PENDIENTE", pageable);

        assertNotNull(resultado);
    }

    @Test
    void suscripciones_deberiaFiltrarPorEstadoActivas() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(suscripciones.findAll(any(), eq(pageable))).thenReturn(new PageImpl<>(List.of(), pageable));

        var resultado = gestion.suscripciones("", "ACTIVAS", pageable);

        assertNotNull(resultado);
    }

    @Test
    void foro_deberiaRetornarPublicacionesSinFiltro() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(foro.findAll(any(), eq(pageable))).thenReturn(new PageImpl<>(List.of(), pageable));

        var resultado = gestion.foro("", pageable);

        assertNotNull(resultado);
    }

    @Test
    void valoraciones_deberiaRechazarSiTrabajadorNoExiste() {
        when(trabajadores.existsById(1L)).thenReturn(false);

        assertThrows(com.al_lado.al_lado_project.exception.ResourceNotFoundException.class,
                () -> gestion.valoraciones(1L, PageRequest.of(0, 10)));
    }

    @Test
    void postulaciones_deberiaRechazarSiOportunidadNoExiste() {
        when(oportunidades.obtenerPorId(999L)).thenThrow(new com.al_lado.al_lado_project.exception.ResourceNotFoundException("Oportunidad no encontrada"));

        assertThrows(com.al_lado.al_lado_project.exception.ResourceNotFoundException.class,
                () -> gestion.postulaciones(999L, PageRequest.of(0, 10)));
    }
}
