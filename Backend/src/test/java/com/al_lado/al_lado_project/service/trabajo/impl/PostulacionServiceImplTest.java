package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.mappers.trabajo.OportunidadLaboralResumenMapper;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import com.al_lado.al_lado_project.repository.comunidad.ValoracionRepository;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios de {@link PostulacionServiceImpl}.
 * Se ejecutan sin contexto Spring ni base de datos: solo se mockean los repositorios
 * y se usa un mapper real (no tiene dependencias de Spring).
 */
@ExtendWith(MockitoExtension.class)
class PostulacionServiceImplTest {

    @Mock
    private PostulacionRepository postulacionRepository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private OportunidadLaboralRepository oportunidadLaboralRepository;

    @Mock
    private ValoracionRepository valoracionRepository;

    @Mock
    private com.al_lado.al_lado_project.service.comunidad.NotificacionService notificacionService;

    private final PostulacionMapper mapper = new PostulacionMapper(new UsuarioResumenMapper(), new OportunidadLaboralResumenMapper());

    private PostulacionServiceImpl service;

    @BeforeEach
    void setUp() {
        // Se construye a mano para no depender de la inyección por nombre de @InjectMocks
        service = new PostulacionServiceImpl(
                postulacionRepository,
                trabajadorRepository,
                oportunidadLaboralRepository,
                valoracionRepository,
                mapper,
                notificacionService);
        // Las operaciones exigen titularidad: autenticar como trabajador 1
        Trabajador auth = new Trabajador();
        auth.setId(1L);
        auth.setEmail("t1@mail.com");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(auth, null, auth.getAuthorities()));
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void crearPostulacion_deberiaGuardarPostulacionCuandoDatosSonValidos() {
        // Dado
        PostulacionDTO dto = new PostulacionDTO();
        dto.setTitulo("Arreglo de llaves");
        dto.setContenido("Se requiere cerrajero para puerta de garaje");
        dto.setAutor(autorConId(1L));
        dto.setOportunidadLaboral(oportunidadConId(1L));
        dto.setRemuneracionPretendida(50000.0);

        Trabajador trabajador = new Trabajador();
        trabajador.setId(1L);

        OportunidadLaboral oportunidad = new OportunidadLaboral();
        oportunidad.setId(1L);
        oportunidad.setTitulo("Arreglo de llaves");
        oportunidad.setActiva(true);

        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(oportunidadLaboralRepository.findById(1L)).thenReturn(Optional.of(oportunidad));
        when(postulacionRepository.existsByOportunidadLaboralIdAndAutorId(1L, 1L)).thenReturn(false);
        when(postulacionRepository.save(any(Postulacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        PostulacionDTO result = service.crearPostulacion(dto);

        // Entonces
        assertNotNull(result);
        assertEquals(Postulacion.ESTADO_PENDIENTE, result.getEstado());
        verify(postulacionRepository, times(1)).save(any(Postulacion.class));
    }

    @Test
    void crearPostulacion_deberiaRechazarOportunidadCerrada() {
        // Dado
        PostulacionDTO dto = new PostulacionDTO();
        dto.setTitulo("Trabajo cerrado");
        dto.setContenido("Ya no está disponible");
        dto.setAutor(autorConId(1L));
        dto.setOportunidadLaboral(oportunidadConId(1L));

        Trabajador trabajador = new Trabajador();
        trabajador.setId(1L);

        OportunidadLaboral oportunidadCerrada = new OportunidadLaboral();
        oportunidadCerrada.setId(1L);
        oportunidadCerrada.setActiva(false);

        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(oportunidadLaboralRepository.findById(1L)).thenReturn(Optional.of(oportunidadCerrada));

        // Cuando / Entonces
        assertThrows(BusinessRuleException.class, () -> service.crearPostulacion(dto));
    }

    @Test
    void completarPostulacion_deberiaFallarCuandoEstadoNoEsSeleccionada() {
        // Dado
        when(postulacionRepository.findById(1L))
                .thenReturn(Optional.of(createPostulacionConEstado(Postulacion.ESTADO_PENDIENTE)));

        // Cuando/Entonces
        assertThrows(BusinessRuleException.class, () -> service.completarPostulacion(1L));
    }

    private Postulacion createPostulacionConEstado(String estado) {
        Postulacion postulacion = new Postulacion();
        postulacion.setEstado(estado);
        return postulacion;
    }

    private UsuarioResumenDTO autorConId(Long id) {
        UsuarioResumenDTO autor = new UsuarioResumenDTO();
        autor.setId(id);
        return autor;
    }

    private OportunidadLaboralResumenDTO oportunidadConId(Long id) {
        OportunidadLaboralResumenDTO oportunidad = new OportunidadLaboralResumenDTO();
        oportunidad.setId(id);
        return oportunidad;
    }
}
