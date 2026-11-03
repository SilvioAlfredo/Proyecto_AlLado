package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.ContratoDTO;
import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionMapper;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.trabajo.ContratoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * HU-08: Historial de contratos del empleador y del trabajador.
 *
 * <p>Pruebas unitarias de {@link ContratoServiceImpl}. Verifica que el empleador
 * vea las postulaciones de sus ofertas, que el trabajador vea sus propias
 * postulaciones con estados de ejecucion, y que no haya acceso ajeno.</p>
 */
@ExtendWith(MockitoExtension.class)
class ContratoServiceImplTest {

    @Mock
    private PostulacionRepository repository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private PostulacionMapper mapper;

    private ContratoService service;

    private Empleador empleador;
    private Trabajador trabajador;
    private OportunidadLaboral oportunidad;
    private Postulacion postulacion;
    private ContratoDTO contratoDTO;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new ContratoServiceImpl(repository, trabajadorRepository, mapper);

        empleador = new Empleador("Empleador Demo", "empleador@demo.com", "password");
        empleador.setId(1L);

        trabajador = new Trabajador("Trabajador Demo", "trabajador@demo.com", "password");
        trabajador.setId(2L);

        oportunidad = new OportunidadLaboral("Albañil", "Reparar pared", empleador, "Barrio Centro", "PRESENCIAL", "$10000");
        oportunidad.setId(10L);

        postulacion = new Postulacion("Postularme", "Tengo experiencia", trabajador, oportunidad, 10000.0);
        postulacion.setId(100L);
        postulacion.setEstado(Postulacion.ESTADO_COMPLETADA);
        postulacion.setFechaCreacion(LocalDateTime.of(2026, 1, 15, 10, 0));

        contratoDTO = new ContratoDTO();
        contratoDTO.setId(100L);
        contratoDTO.setEstado(Postulacion.ESTADO_COMPLETADA);
        contratoDTO.setFechaCreacion(LocalDateTime.of(2026, 1, 15, 10, 0));

        UsuarioResumenDTO autorResumen = new UsuarioResumenDTO();
        autorResumen.setId(2L);
        autorResumen.setNombre("Trabajador Demo");
        autorResumen.setEmail("trabajador@demo.com");
        contratoDTO.setTrabajador(autorResumen);

        OportunidadLaboralResumenDTO oportunidadResumen = new OportunidadLaboralResumenDTO();
        oportunidadResumen.setId(10L);
        oportunidadResumen.setTitulo("Albañil");
        contratoDTO.setOportunidad(oportunidadResumen);

        UsuarioResumenDTO empleadorResumen = new UsuarioResumenDTO();
        empleadorResumen.setId(1L);
        empleadorResumen.setNombre("Empleador Demo");
        empleadorResumen.setEmail("empleador@demo.com");
        contratoDTO.setEmpleador(empleadorResumen);
    }

    private void autenticarComoEmpleador() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(empleador, null, empleador.getAuthorities()));
    }

    private void autenticarComoTrabajador() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(trabajador, null, trabajador.getAuthorities()));
    }

    private void autenticarComoTercero() {
        Usuario tercero = new Trabajador("Tercero", "tercero@demo.com", "password");
        tercero.setId(99L);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(tercero, null, tercero.getAuthorities()));
    }

    private void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void historialEmpleador_deberiaRetornarContratosDeSusOfertas() {
        autenticarComoEmpleador();
        when(repository.findByOportunidadLaboralAutorId(1L)).thenReturn(List.of(postulacion));

        List<ContratoDTO> resultado = service.historialEmpleador();

        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).getId());
        assertEquals(Postulacion.ESTADO_COMPLETADA, resultado.get(0).getEstado());
        verify(repository, times(1)).findByOportunidadLaboralAutorId(1L);
    }

    @Test
    void historialEmpleador_deberiaRechazarSiNoHaySesion() {
        limpiarContexto();

        assertThrows(AccessDeniedException.class, () -> service.historialEmpleador());
    }

    @Test
    void historialEmpleador_deberiaRechazarSiNoEsEmpleador() {
        autenticarComoTrabajador();

        assertThrows(AccessDeniedException.class, () -> service.historialEmpleador());
    }

    @Test
    void historialTrabajador_deberiaRetornarContratosDelTrabajador() {
        autenticarComoTrabajador();
        when(trabajadorRepository.findById(2L)).thenReturn(Optional.of(trabajador));
        when(repository.findByAutorId(2L)).thenReturn(List.of(postulacion));

        List<ContratoDTO> resultado = service.historialTrabajador(2L);

        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).getId());
        verify(trabajadorRepository, times(1)).findById(2L);
        verify(repository, times(1)).findByAutorId(2L);
    }

    @Test
    void historialTrabajador_deberiaExcluirPostulacionesPendientes() {
        autenticarComoTrabajador();

        Postulacion pendiente = new Postulacion("P", "C", trabajador, oportunidad, 5000.0);
        pendiente.setId(200L);
        pendiente.setEstado(Postulacion.ESTADO_PENDIENTE);
        pendiente.setFechaCreacion(LocalDateTime.of(2026, 2, 1, 10, 0));

        when(trabajadorRepository.findById(2L)).thenReturn(Optional.of(trabajador));
        when(repository.findByAutorId(2L)).thenReturn(List.of(postulacion, pendiente));

        List<ContratoDTO> resultado = service.historialTrabajador(2L);

        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).getId());
        assertEquals(Postulacion.ESTADO_COMPLETADA, resultado.get(0).getEstado());
    }

    @Test
    void historialTrabajador_deberiaRechazarSiTrabajadorNoExiste() {
        autenticarComoTrabajador();
        when(trabajadorRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.historialTrabajador(2L));
    }

    @Test
    void historialTrabajador_deberiaRechazarAccesoAjeno() {
        autenticarComoEmpleador();

        assertThrows(AccessDeniedException.class, () -> service.historialTrabajador(2L));
        verify(trabajadorRepository, never()).findById(anyLong());
    }

    @Test
    void obtenerPorId_deberiaRetornarContratoParaElTitular() {
        autenticarComoEmpleador();
        when(repository.findById(100L)).thenReturn(Optional.of(postulacion));

        ContratoDTO resultado = service.obtenerPorId(100L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        verify(repository, times(1)).findById(100L);
    }

    @Test
    void obtenerPorId_deberiaRechazarSiContratoNoExiste() {
        autenticarComoEmpleador();
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    void obtenerPorId_deberiaRechazarAccesoAjeno() {
        autenticarComoTercero();
        when(repository.findById(100L)).thenReturn(Optional.of(postulacion));

        assertThrows(AccessDeniedException.class, () -> service.obtenerPorId(100L));
    }

    @Test
    void historialEmpleador_deberiaOrdenarPorFechaDescendente() {
        autenticarComoEmpleador();

        Postulacion masReciente = new Postulacion("R", "C", trabajador, oportunidad, 8000.0);
        masReciente.setId(300L);
        masReciente.setEstado(Postulacion.ESTADO_SELECCIONADA);
        masReciente.setFechaCreacion(LocalDateTime.of(2026, 3, 1, 10, 0));

        when(repository.findByOportunidadLaboralAutorId(1L)).thenReturn(List.of(postulacion, masReciente));
        ContratoDTO dtoReciente = new ContratoDTO();
        dtoReciente.setId(300L);
        dtoReciente.setFechaCreacion(LocalDateTime.of(2026, 3, 1, 10, 0));

        List<ContratoDTO> resultado = service.historialEmpleador();

        assertEquals(2, resultado.size());
        assertEquals(300L, resultado.get(0).getId());
        assertEquals(100L, resultado.get(1).getId());
    }
}
