package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionMapper;
import com.al_lado.al_lado_project.repository.comunidad.ValoracionRepository;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.service.comunidad.NotificacionService;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AL-TEST-001: titularidad en postulaciones (titular/admin/terceros).
 * Estilo Mockito sin contexto Spring, como PostulacionServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class PostulacionAuthorizationTest {

    @Mock
    private PostulacionRepository postulacionRepository;
    @Mock
    private TrabajadorRepository trabajadorRepository;
    @Mock
    private OportunidadLaboralRepository oportunidadLaboralRepository;
    @Mock
    private ValoracionRepository valoracionRepository;
    @Mock
    private PostulacionMapper mapper;
    @Mock
    private NotificacionService notificacionService;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private static void autenticarComo(com.al_lado.al_lado_project.entity.usuario.Usuario u) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities()));
    }

    private static Trabajador trabajador(Long id) {
        Trabajador t = new Trabajador();
        t.setId(id);
        t.setEmail("t" + id + "@mail.com");
        return t;
    }

    private static Empleador empleador(Long id) {
        Empleador e = new Empleador();
        e.setId(id);
        e.setEmail("e" + id + "@mail.com");
        return e;
    }

    private static PostulacionDTO dtoPostulacion(Long autorId, Long oportunidadId) {
        PostulacionDTO dto = new PostulacionDTO();
        dto.setTitulo("Ayuda");
        dto.setContenido("Contenido");
        UsuarioResumenDTO autor = new UsuarioResumenDTO();
        autor.setId(autorId);
        dto.setAutor(autor);
        OportunidadLaboralResumenDTO op = new OportunidadLaboralResumenDTO();
        op.setId(oportunidadId);
        dto.setOportunidadLaboral(op);
        return dto;
    }

    private PostulacionServiceImpl service() {
        return new PostulacionServiceImpl(postulacionRepository, trabajadorRepository,
                oportunidadLaboralRepository, valoracionRepository, mapper, notificacionService);
    }

    @Test
    void terceroNoPuedePostularComoOtroTrabajador() {
        autenticarComo(trabajador(1L));

        assertThrows(AccessDeniedException.class,
                () -> service().crearPostulacion(dtoPostulacion(2L, 10L)));
        verify(postulacionRepository, never()).save(any());
    }

    @Test
    void titularSiPuedeCrearSuPostulacion() {
        autenticarComo(trabajador(1L));
        OportunidadLaboral op = new OportunidadLaboral();
        op.setId(10L);
        op.setActiva(true);
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador(1L)));
        when(oportunidadLaboralRepository.findById(10L)).thenReturn(Optional.of(op));
        when(postulacionRepository.existsByOportunidadLaboralIdAndAutorId(10L, 1L)).thenReturn(false);
        when(mapper.toEntity(any(PostulacionDTO.class))).thenReturn(new Postulacion());
        when(postulacionRepository.save(any(Postulacion.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDTO(any(Postulacion.class))).thenReturn(new PostulacionDTO());

        PostulacionDTO resultado = service().crearPostulacion(dtoPostulacion(1L, 10L));
        org.junit.jupiter.api.Assertions.assertNotNull(resultado);
        verify(postulacionRepository).save(any(Postulacion.class));
    }

    @Test
    void empleadorNoDuenoNoPuedeSeleccionar() {
        autenticarComo(empleador(5L));
        OportunidadLaboral op = new OportunidadLaboral();
        op.setId(10L);
        op.setAutor(empleador(9L));
        Postulacion p = new Postulacion();
        p.setId(3L);
        p.setEstado(Postulacion.ESTADO_PENDIENTE);
        p.setOportunidadLaboral(op);
        when(postulacionRepository.findById(3L)).thenReturn(Optional.of(p));

        assertThrows(AccessDeniedException.class, () -> service().seleccionarPostulacion(3L));
        verify(postulacionRepository, never()).save(any());
    }

    @Test
    void empleadorDuenoSiPuedeSeleccionarPendiente() {
        autenticarComo(empleador(9L));
        OportunidadLaboral op = new OportunidadLaboral();
        op.setId(10L);
        op.setAutor(empleador(9L));
        Postulacion p = new Postulacion();
        p.setId(3L);
        p.setEstado(Postulacion.ESTADO_PENDIENTE);
        p.setOportunidadLaboral(op);
        when(postulacionRepository.findById(3L)).thenReturn(Optional.of(p));
        when(postulacionRepository.save(any(Postulacion.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDTO(any(Postulacion.class))).thenReturn(new PostulacionDTO());

        service().seleccionarPostulacion(3L);
        assertEquals(Postulacion.ESTADO_SELECCIONADA, p.getEstado());
    }

    @Test
    void completarDesdePendienteEsIlegal() {
        autenticarComo(empleador(9L));
        OportunidadLaboral op = new OportunidadLaboral();
        op.setId(10L);
        op.setAutor(empleador(9L));
        Postulacion p = new Postulacion();
        p.setId(3L);
        p.setEstado(Postulacion.ESTADO_PENDIENTE);
        p.setOportunidadLaboral(op);
        when(postulacionRepository.findById(3L)).thenReturn(Optional.of(p));

        assertThrows(BusinessRuleException.class, () -> service().completarPostulacion(3L));
    }
}
