package com.al_lado.al_lado_project.service.comunidad.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.al_lado.al_lado_project.dto.comunidad.PublicidadDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.comunidad.Publicidad;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.mappers.comunidad.PublicidadMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import com.al_lado.al_lado_project.repository.comunidad.PublicidadRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.service.comunidad.PublicidadService;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;

@ExtendWith(MockitoExtension.class)
class PublicidadServiceImplTest {

    @Mock private PublicidadRepository publicidadRepository;
    @Mock private EmpleadorRepository empleadorRepository;
    @Mock private SuscripcionService suscripcionService;
    private final PublicidadMapper mapper = new PublicidadMapper(new UsuarioResumenMapper());
    private PublicidadService service;

    private Empleador empleador;
    private Publicidad publicidad;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new PublicidadServiceImpl(publicidadRepository, empleadorRepository, mapper, suscripcionService);

        empleador = new Empleador();
        empleador.setId(1L);
        empleador.setNombre("Empleador PRO");
        empleador.setEmail("emp@test.com");
        empleador.setPassword("x");
        // La sesion es del propio empleador anunciante (titular).
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(empleador, null, empleador.getAuthorities()));

        publicidad = new Publicidad();
        publicidad.setId(1L);
        publicidad.setAnunciante(empleador);
        publicidad.setTitulo("Anuncio de prueba");
        publicidad.setActiva(true);
    }

    @AfterEach
    void limpiarContexto() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void guardar_deberiaCrearPublicidadCuandoEmpleadorTienePRO() {
        // Dado
        when(suscripcionService.esProVigente(1L)).thenReturn(true);
        when(empleadorRepository.findById(1L)).thenReturn(Optional.of(empleador));
        when(publicidadRepository.save(any(Publicidad.class))).thenReturn(publicidad);

        // Cuando
        PublicidadDTO resultado = service.guardar(publicidadDTO());

        // Entonces
        assertNotNull(resultado);
        verify(publicidadRepository, times(1)).save(any(Publicidad.class));
    }

    @Test
    void guardar_deberiaRechazarCuandoEmpleadorNoTienePRO() {
        // Dado
        when(suscripcionService.esProVigente(1L)).thenReturn(false);

        // Cuando
        PublicidadDTO dto = publicidadDTO();
        dto.setTitulo("Anuncio");
        assertThrows(BusinessRuleException.class, () -> service.guardar(dto));

        // Entonces
        verify(publicidadRepository, never()).save(any(Publicidad.class));
    }

    @Test
    void obtenerVigentes_deberiaRetornarSoloAnunciosActivos() {
        // Dado
        when(publicidadRepository.buscarPublicidadesVigentes(any(LocalDate.class)))
                .thenReturn(List.of(publicidad));

        // Cuando
        var resultado = service.obtenerVigentes();

        // Entonces
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    private PublicidadDTO publicidadDTO() {
        PublicidadDTO dto = new PublicidadDTO();
        dto.setTitulo("Anuncio de prueba");
        dto.setContenido("Contenido del anuncio");
        dto.setFechaInicio(LocalDate.of(2026, 1, 1));
        dto.setFechaFin(LocalDate.of(2026, 12, 31));
        UsuarioResumenDTO anunciante = new UsuarioResumenDTO();
        anunciante.setId(1L);
        dto.setAnunciante(anunciante);
        return dto;
    }
}
