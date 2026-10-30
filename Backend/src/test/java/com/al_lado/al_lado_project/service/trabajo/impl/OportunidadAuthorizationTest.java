package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.mappers.trabajo.OportunidadLaboralMapper;
import com.al_lado.al_lado_project.repository.trabajo.EtiquetaRepository;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** AL-TEST-001: titularidad en ofertas (dueño/admin/terceros). */
@ExtendWith(MockitoExtension.class)
class OportunidadAuthorizationTest {

    @Mock
    private OportunidadLaboralRepository repository;
    @Mock
    private EmpleadorRepository empleadorRepository;
    @Mock
    private EtiquetaRepository etiquetaRepository;
    @Mock
    private OportunidadLaboralMapper mapper;
    @Mock
    private TrabajadorRepository trabajadorRepository;
    @Mock
    private SuscripcionService suscripcionService;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private static void autenticarComo(com.al_lado.al_lado_project.entity.usuario.Usuario u) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities()));
    }

    private static Empleador empleador(Long id) {
        Empleador e = new Empleador();
        e.setId(id);
        e.setEmail("e" + id + "@mail.com");
        return e;
    }

    private OportunidadLaboralServiceImpl service() {
        return new OportunidadLaboralServiceImpl(repository, empleadorRepository,
                etiquetaRepository, mapper, trabajadorRepository, suscripcionService);
    }

    private static OportunidadLaboralDTO dto(Long id, Long autorId) {
        OportunidadLaboralDTO dto = new OportunidadLaboralDTO();
        dto.setId(id);
        dto.setTitulo("T");
        dto.setContenido("C");
        UsuarioResumenDTO autor = new UsuarioResumenDTO();
        autor.setId(autorId);
        dto.setAutor(autor);
        return dto;
    }

    @Test
    void terceroNoPuedeActualizarOfertaAjena() {
        autenticarComo(empleador(5L));
        OportunidadLaboral existente = new OportunidadLaboral();
        existente.setId(100L);
        existente.setAutor(empleador(9L));
        when(empleadorRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(empleador(5L)));
        when(repository.findById(100L)).thenReturn(Optional.of(existente));

        assertThrows(AccessDeniedException.class, () -> service().guardar(dto(100L, 5L)));
        verify(repository, never()).save(any());
    }

    @Test
    void titularSiPuedeCrearOferta() {
        autenticarComo(empleador(5L));
        when(empleadorRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(empleador(5L)));
        when(mapper.toEntity(any(OportunidadLaboralDTO.class))).thenReturn(new OportunidadLaboral());
        when(suscripcionService.puedePublicarOferta(5L)).thenReturn(true);
        when(repository.save(any(OportunidadLaboral.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDTO(any(OportunidadLaboral.class))).thenReturn(new OportunidadLaboralDTO());

        assertNotNull(service().guardar(dto(null, 5L)));
        verify(repository).save(any(OportunidadLaboral.class));
    }

    @Test
    void reactivarOfertaConsultaCupoAntesDeGuardar() {
        autenticarComo(empleador(5L));
        OportunidadLaboral existente = new OportunidadLaboral();
        existente.setId(100L);
        existente.setActiva(false);
        existente.setAutor(empleador(5L));
        when(empleadorRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(empleador(5L)));
        when(repository.findById(100L)).thenReturn(Optional.of(existente));
        when(suscripcionService.puedePublicarOferta(5L)).thenReturn(false);

        OportunidadLaboralDTO actualizacion = dto(100L, 5L);
        actualizacion.setActiva(true);
        assertThrows(BusinessRuleException.class, () -> service().guardar(actualizacion));
        verify(suscripcionService, times(1)).puedePublicarOferta(5L);
        verify(repository, never()).save(any(OportunidadLaboral.class));
    }

    @Test
    void terceroNoPuedeEliminarOfertaAjena() {
        autenticarComo(empleador(5L));
        OportunidadLaboral existente = new OportunidadLaboral();
        existente.setId(100L);
        existente.setAutor(empleador(9L));
        when(repository.findById(100L)).thenReturn(Optional.of(existente));

        assertThrows(AccessDeniedException.class, () -> service().eliminar(100L));
        verify(repository, never()).delete(any(OportunidadLaboral.class));
    }
}
