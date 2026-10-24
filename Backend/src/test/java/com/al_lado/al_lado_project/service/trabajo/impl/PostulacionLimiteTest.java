package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RF-04 / HU-02 CA-01: valor limite de la remuneracion pretendida.
 *
 * <p><b>DEFECTO ABIERTO (TC-POS-004).</b> El requerimiento exige que una
 * remuneracion menor o igual a cero se rechace, pero {@code PostulacionDTO} no
 * declara {@code @Positive} sobre {@code remuneracionPretendida} y
 * {@code PostulacionServiceImpl.crearPostulacion} no la valida. Estos tests
 * dejan constancia del comportamiento actual para que, cuando se corrija,
 * cambien de verdugo y documenten el defecto.</p>
 */
@ExtendWith(MockitoExtension.class)
class PostulacionLimiteTest {

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

    private final PostulacionMapper mapper = new PostulacionMapper(
            new UsuarioResumenMapper(), new OportunidadLaboralResumenMapper());

    private PostulacionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PostulacionServiceImpl(
                postulacionRepository,
                trabajadorRepository,
                oportunidadLaboralRepository,
                valoracionRepository,
                mapper,
                notificacionService);

        Trabajador auth = new Trabajador();
        auth.setId(1L);
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
    void crearPostulacion_deberiaAceptarRemuneracionCeroHoy() {
        // Dado: remuneracion 0, que el requerimiento deberia rechazar.
        PostulacionDTO dto = postulacionDTO(0.0);

        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador()));
        when(oportunidadLaboralRepository.findById(5L)).thenReturn(Optional.of(oportunidadActiva()));
        when(postulacionRepository.existsByOportunidadLaboralIdAndAutorId(5L, 1L)).thenReturn(false);
        when(postulacionRepository.save(any(Postulacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        PostulacionDTO resultado = service.crearPostulacion(dto);

        // Entonces: hoy la persistencia ocurre (defecto TC-POS-004).
        assertEquals(0.0, resultado.getRemuneracionPretendida());
        verify(postulacionRepository, times(1)).save(any(Postulacion.class));
    }

    @Test
    void crearPostulacion_deberiaAceptarRemuneracionNegativaHoy() {
        // Dado: remuneracion negativa.
        PostulacionDTO dto = postulacionDTO(-5000.0);

        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador()));
        when(oportunidadLaboralRepository.findById(5L)).thenReturn(Optional.of(oportunidadActiva()));
        when(postulacionRepository.existsByOportunidadLaboralIdAndAutorId(5L, 1L)).thenReturn(false);
        when(postulacionRepository.save(any(Postulacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        PostulacionDTO resultado = service.crearPostulacion(dto);

        // Entonces: hoy la persistencia ocurre (defecto TC-POS-004).
        assertEquals(-5000.0, resultado.getRemuneracionPretendida());
        verify(postulacionRepository, times(1)).save(any(Postulacion.class));
    }

    @Test
    void crearPostulacion_deberiaAceptarRemuneracionValida() {
        // Dado
        PostulacionDTO dto = postulacionDTO(45000.0);

        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador()));
        when(oportunidadLaboralRepository.findById(5L)).thenReturn(Optional.of(oportunidadActiva()));
        when(postulacionRepository.existsByOportunidadLaboralIdAndAutorId(5L, 1L)).thenReturn(false);
        when(postulacionRepository.save(any(Postulacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        PostulacionDTO resultado = service.crearPostulacion(dto);

        // Entonces
        assertEquals(45000.0, resultado.getRemuneracionPretendida());
        assertEquals(Postulacion.ESTADO_PENDIENTE, resultado.getEstado());
    }

    private PostulacionDTO postulacionDTO(Double remuneracion) {
        PostulacionDTO dto = new PostulacionDTO();
        dto.setTitulo("Propuesta de prueba");
        dto.setContenido("Propuesta para una oportunidad activa");
        UsuarioResumenDTO autor = new UsuarioResumenDTO();
        autor.setId(1L);
        dto.setAutor(autor);
        OportunidadLaboralResumenDTO oportunidad = new OportunidadLaboralResumenDTO();
        oportunidad.setId(5L);
        dto.setOportunidadLaboral(oportunidad);
        dto.setRemuneracionPretendida(remuneracion);
        return dto;
    }

    private Trabajador trabajador() {
        Trabajador trabajador = new Trabajador();
        trabajador.setId(1L);
        trabajador.setNombre("Demo Trabajador");
        trabajador.setEmail("trabajador@demo.com");
        trabajador.setPassword("x");
        return trabajador;
    }

    private OportunidadLaboral oportunidadActiva() {
        OportunidadLaboral oportunidad = new OportunidadLaboral();
        oportunidad.setId(5L);
        oportunidad.setTitulo("Reparar persiana");
        oportunidad.setActiva(true);
        Empleador autor = new Empleador();
        autor.setId(2L);
        autor.setNombre("Demo Empleador");
        autor.setEmail("empleador@demo.com");
        autor.setPassword("x");
        oportunidad.setAutor(autor);
        return oportunidad;
    }
}
