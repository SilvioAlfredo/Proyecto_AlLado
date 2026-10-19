package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** HU trabajador: retirar la propia postulación mientras siga abierta. */
@ExtendWith(MockitoExtension.class)
class PostulacionRetirarTest {

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

    private final PostulacionMapper mapper =
            new PostulacionMapper(new UsuarioResumenMapper(), new OportunidadLaboralResumenMapper());

    private PostulacionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PostulacionServiceImpl(
                postulacionRepository, trabajadorRepository, oportunidadLaboralRepository,
                valoracionRepository, mapper, notificacionService);
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

    private Postulacion postulacionEnEstado(String estado) {
        OportunidadLaboral op = new OportunidadLaboral();
        op.setId(10L);
        Empleador empleador = new Empleador();
        empleador.setId(9L);
        op.setAutor(empleador);
        Postulacion p = new Postulacion();
        p.setId(5L);
        p.setEstado(estado);
        Trabajador t = new Trabajador();
        t.setId(1L);
        p.setAutor(t);
        p.setOportunidadLaboral(op);
        return p;
    }

    @Test
    void titularRetiraPendiente() {
        when(postulacionRepository.findById(5L)).thenReturn(Optional.of(postulacionEnEstado("PENDIENTE")));
        when(trabajadorRepository.existsById(1L)).thenReturn(true);
        when(postulacionRepository.save(any(Postulacion.class))).thenAnswer(i -> i.getArgument(0));

        PostulacionDTO dto = service.retirarPostulacion(5L);

        assertEquals("RETIRADA", dto.getEstado());
        verify(postulacionRepository).save(any(Postulacion.class));
    }

    @Test
    void retirarCompletadaEsIlegal() {
        when(postulacionRepository.findById(5L)).thenReturn(Optional.of(postulacionEnEstado("COMPLETADA")));

        assertThrows(BusinessRuleException.class, () -> service.retirarPostulacion(5L));
    }

    @Test
    void terceroNoPuedeRetirarAjena() {
        Postulacion ajena = postulacionEnEstado("PENDIENTE");
        Trabajador otro = new Trabajador();
        otro.setId(2L);
        ajena.setAutor(otro);
        when(postulacionRepository.findById(5L)).thenReturn(Optional.of(ajena));

        assertThrows(AccessDeniedException.class, () -> service.retirarPostulacion(5L));
    }
}
