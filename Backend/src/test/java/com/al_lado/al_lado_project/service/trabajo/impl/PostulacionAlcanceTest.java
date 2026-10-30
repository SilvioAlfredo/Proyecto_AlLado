package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.mappers.trabajo.OportunidadLaboralResumenMapper;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AL-SEC-006 (MEJ-001): {@code GET /postulaciones} y {@code GET /postulaciones/{id}}
 * se acotan al alcance del lector.
 *
 * <p>El trabajador ve las suyas, el empleador las de sus ofertas, el administrador
 * todas; nadie más consulta las postulaciones de terceros.</p>
 */
@ExtendWith(MockitoExtension.class)
class PostulacionAlcanceTest {

    @Mock
    private PostulacionRepository postulacionRepository;
    @Mock
    private TrabajadorRepository trabajadorRepository;
    @Mock
    private OportunidadLaboralRepository oportunidadLaboralRepository;
    @Mock
    private ValoracionRepository valoracionRepository;
    @Mock
    private NotificacionService notificacionService;

    private PostulacionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PostulacionServiceImpl(postulacionRepository, trabajadorRepository,
                oportunidadLaboralRepository, valoracionRepository,
                new PostulacionMapper(new UsuarioResumenMapper(), new OportunidadLaboralResumenMapper()),
                notificacionService);
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private void iniciarSesion(Usuario usuario) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    private static Trabajador trabajador(Long id) {
        Trabajador t = new Trabajador();
        t.setId(id);
        t.setNombre("Trabajador " + id);
        t.setEmail("trabajador" + id + "@mail.com");
        return t;
    }

    private static Empleador empleador(Long id) {
        Empleador e = new Empleador();
        e.setId(id);
        e.setNombre("Empresa " + id);
        e.setEmail("empleador" + id + "@mail.com");
        return e;
    }

    private static Postulacion postulacion(Long id, Trabajador autor, Empleador dueno) {
        OportunidadLaboral oferta = new OportunidadLaboral();
        oferta.setId(id + 100);
        oferta.setTitulo("Oferta " + id);
        oferta.setAutor(dueno);

        Postulacion p = new Postulacion("Postulación " + id, "Contenido", autor, oferta, 50.0);
        p.setId(id);
        p.setAutor(autor);
        p.setOportunidadLaboral(oferta);
        return p;
    }

    @Test
    void elTrabajadorSoloVeSusPostulaciones() {
        Trabajador propio = trabajador(1L);
        Postulacion mia = postulacion(11L, propio, empleador(5L));
        when(postulacionRepository.findByAutorId(1L)).thenReturn(List.of(mia));
        iniciarSesion(propio);

        List<PostulacionDTO> resultado = service.obtenerTodas();

        assertEquals(1, resultado.size());
        assertEquals(11L, resultado.get(0).getId());
        verify(postulacionRepository, never()).findAll();
    }

    @Test
    void elEmpleadorVeLasPostulacionesDeSusOfertas() {
        Empleador dueno = empleador(5L);
        Postulacion recibida = postulacion(11L, trabajador(1L), dueno);
        when(postulacionRepository.findByOportunidadLaboralAutorId(5L)).thenReturn(List.of(recibida));
        iniciarSesion(dueno);

        List<PostulacionDTO> resultado = service.obtenerTodas();

        assertEquals(1, resultado.size());
        verify(postulacionRepository, never()).findAll();
    }

    @Test
    void elAdministradorVeTodas() {
        Administrador admin = new Administrador();
        admin.setId(9L);
        admin.setEmail("admin@al-lado.com");
        when(postulacionRepository.findAll())
                .thenReturn(List.of(postulacion(11L, trabajador(1L), empleador(5L))));
        iniciarSesion(admin);

        assertEquals(1, service.obtenerTodas().size());
    }

    @Test
    void nadieRecibeElListadoCompletoDeTerceros() {
        Trabajador ajeno = trabajador(2L);
        when(postulacionRepository.findByAutorId(2L)).thenReturn(List.of());
        iniciarSesion(ajeno);

        assertTrue(service.obtenerTodas().isEmpty());
        verify(postulacionRepository, never()).findAll();
    }

    @Test
    void sinSesionElListadoDePostulacionesSeDeniega() {
        assertThrows(AccessDeniedException.class, () -> service.obtenerTodas());
        verify(postulacionRepository, never()).findAll();
    }

    @Test
    void elAutorPuedeConsultarSuPostulacion() {
        Trabajador autor = trabajador(1L);
        when(postulacionRepository.findById(11L))
                .thenReturn(java.util.Optional.of(postulacion(11L, autor, empleador(5L))));
        iniciarSesion(autor);

        PostulacionDTO dto = service.obtenerPorId(11L);

        assertEquals(11L, dto.getId());
    }

    @Test
    void elEmpleadorDeLaOfertaPuedeConsultarLaPostulacion() {
        Empleador dueno = empleador(5L);
        when(postulacionRepository.findById(11L))
                .thenReturn(java.util.Optional.of(postulacion(11L, trabajador(1L), dueno)));
        iniciarSesion(dueno);

        assertEquals(11L, service.obtenerPorId(11L).getId());
    }

    @Test
    void unTerceroNoPuedeConsultarLaPostulacion() {
        when(postulacionRepository.findById(11L))
                .thenReturn(java.util.Optional.of(postulacion(11L, trabajador(1L), empleador(5L))));
        iniciarSesion(trabajador(2L));

        assertThrows(AccessDeniedException.class, () -> service.obtenerPorId(11L));
    }

    @Test
    void elAdministradorPuedeConsultarCualquierPostulacion() {
        Administrador admin = new Administrador();
        admin.setId(9L);
        admin.setEmail("admin@al-lado.com");
        when(postulacionRepository.findById(11L))
                .thenReturn(java.util.Optional.of(postulacion(11L, trabajador(1L), empleador(5L))));
        iniciarSesion(admin);

        assertEquals(11L, service.obtenerPorId(11L).getId());
    }

    @Test
    void unaPostulacionInexistenteSigueSiendo404() {
        when(postulacionRepository.findById(404L)).thenReturn(java.util.Optional.empty());
        iniciarSesion(trabajador(1L));

        assertThrows(com.al_lado.al_lado_project.exception.ResourceNotFoundException.class,
                () -> service.obtenerPorId(404L));
    }

    @Test
    void unaOtraPostulacionNoSePuedeConsultarAunExistiendo() {
        // El 403 no delata si la postulación existe o no: mismo camino para terceros.
        when(postulacionRepository.findById(11L))
                .thenReturn(java.util.Optional.of(postulacion(11L, trabajador(1L), empleador(5L))));
        Empleador ajeno = empleador(77L);

        iniciarSesion(ajeno);

        assertThrows(AccessDeniedException.class, () -> service.obtenerPorId(11L));
        verify(postulacionRepository, never()).findAll();
        verify(postulacionRepository, never()).findByAutorId(any());
    }

    // ==============================================================
    // AL-SEC-005: las estadísticas por email tampoco enumeran cuentas
    // ==============================================================

    @Test
    void lasEstadisticasPorEmailResponden403TantoSiNoExisteSiEsDeOtro() {
        iniciarSesion(trabajador(1L));
        when(trabajadorRepository.findByEmail("nadie@al-lado.com")).thenReturn(java.util.Optional.empty());
        when(trabajadorRepository.findByEmail("otro@al-lado.com"))
                .thenReturn(java.util.Optional.of(trabajador(2L)));

        // Misma respuesta (403) exista o no el email: un 404 dejaría comparar
        // qué direcciones están registradas.
        assertThrows(AccessDeniedException.class,
                () -> service.obtenerEstadisticasTrabajadorPorEmail("nadie@al-lado.com"));
        assertThrows(AccessDeniedException.class,
                () -> service.obtenerEstadisticasTrabajadorPorEmail("otro@al-lado.com"));
    }

    @Test
    void lasEstadisticasPorEmailPropiasSiSePuedenConsultar() {
        Trabajador propio = trabajador(1L);
        propio.setEmail("demo@al-lado.com");
        iniciarSesion(propio);
        when(trabajadorRepository.findByEmail("demo@al-lado.com")).thenReturn(java.util.Optional.of(propio));

        assertNotNull(service.obtenerEstadisticasTrabajadorPorEmail("demo@al-lado.com"));
    }
}
