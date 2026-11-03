package com.al_lado.al_lado_project.service.perfil.impl;

import com.al_lado.al_lado_project.dto.perfil.HorarioDisponibilidadDTO;
import com.al_lado.al_lado_project.entity.perfil.HorarioDisponibilidad;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.perfil.HorarioDisponibilidadMapper;
import com.al_lado.al_lado_project.repository.perfil.HorarioDisponibilidadRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.perfil.HorarioDisponibilidadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * HU-10: Configurar dias y rangos horarios de servicio (Trabajador).
 *
 * <p>Pruebas unitarias de {@link HorarioDisponibilidadServiceImpl}. Valida
 * creacion de horarios, deteccion de solapamiento, control de titularidad y
 * listado por trabajador.</p>
 */
@ExtendWith(MockitoExtension.class)
class HorarioDisponibilidadServiceImplTest {

    @Mock
    private HorarioDisponibilidadRepository repository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private HorarioDisponibilidadMapper mapper;

    private HorarioDisponibilidadService service;

    private Trabajador trabajador;
    private HorarioDisponibilidadDTO dtoValido;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new HorarioDisponibilidadServiceImpl(repository, trabajadorRepository, mapper);

        trabajador = new Trabajador("Trabajador Demo", "trabajador@demo.com", "password");
        trabajador.setId(1L);

        dtoValido = new HorarioDisponibilidadDTO();
        dtoValido.setDiaSemana("Lunes");
        dtoValido.setHoraInicio(LocalTime.of(9, 0));
        dtoValido.setHoraFin(LocalTime.of(12, 0));
    }

    private void autenticarComoTrabajador() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(trabajador, null, trabajador.getAuthorities()));
    }

    private void autenticarComoOtro() {
        Trabajador otro = new Trabajador("Otro", "otro@demo.com", "password");
        otro.setId(99L);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(otro, null, otro.getAuthorities()));
    }

    @Test
    void obtenerPorTrabajador_deberiaRetornarHorariosDelTrabajador() {
        when(repository.findByTrabajadorId(1L)).thenReturn(List.of(new HorarioDisponibilidad()));
        when(mapper.toDTO(any())).thenReturn(dtoValido);

        List<HorarioDisponibilidadDTO> resultado = service.obtenerPorTrabajador(1L);

        assertEquals(1, resultado.size());
        assertEquals(dtoValido, resultado.get(0));
        verify(repository, times(1)).findByTrabajadorId(1L);
    }

    @Test
    void agregar_deberiaCrearHorarioValido() {
        autenticarComoTrabajador();
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(repository.findByTrabajadorIdAndDiaSemanaIgnoreCase(1L, "Lunes")).thenReturn(List.of());
        HorarioDisponibilidad entidad = new HorarioDisponibilidad();
        when(mapper.toEntity(dtoValido)).thenReturn(entidad);
        when(repository.save(entidad)).thenReturn(entidad);
        when(mapper.toDTO(entidad)).thenReturn(dtoValido);

        HorarioDisponibilidadDTO resultado = service.agregar(1L, dtoValido);

        assertNotNull(resultado);
        verify(repository, times(1)).save(entidad);
    }

    @Test
    void agregar_deberiaRechazarHorasNulas() {
        dtoValido.setHoraInicio(null);
        autenticarComoTrabajador();

        assertThrows(BusinessRuleException.class, () -> service.agregar(1L, dtoValido));
        verify(repository, never()).save(any());
    }

    @Test
    void agregar_deberiaRechazarHoraFinAntesDeInicio() {
        dtoValido.setHoraInicio(LocalTime.of(12, 0));
        dtoValido.setHoraFin(LocalTime.of(9, 0));
        autenticarComoTrabajador();

        assertThrows(BusinessRuleException.class, () -> service.agregar(1L, dtoValido));
    }

    @Test
    void agregar_deberiaRechazarDiaSemanaVacio() {
        dtoValido.setDiaSemana("   ");
        autenticarComoTrabajador();

        assertThrows(BusinessRuleException.class, () -> service.agregar(1L, dtoValido));
    }

    @Test
    void agregar_deberiaRechazarSolapamientoMismoDia() {
        HorarioDisponibilidad existente = new HorarioDisponibilidad();
        existente.setHoraInicio(LocalTime.of(10, 0));
        existente.setHoraFin(LocalTime.of(11, 0));

        autenticarComoTrabajador();
        when(repository.findByTrabajadorIdAndDiaSemanaIgnoreCase(1L, "Lunes")).thenReturn(List.of(existente));

        assertThrows(BusinessRuleException.class, () -> service.agregar(1L, dtoValido));
    }

    @Test
    void agregar_deberiaRechazarSiTrabajadorNoExiste() {
        autenticarComoTrabajador();
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.agregar(1L, dtoValido));
    }

    @Test
    void agregar_deberiaRechazarAccesoAjeno() {
        autenticarComoOtro();

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.agregar(1L, dtoValido));
    }

    @Test
    void eliminar_deberiaEliminarHorarioDelTitular() {
        HorarioDisponibilidad horario = new HorarioDisponibilidad();
        horario.setId(1L);
        horario.setTrabajador(trabajador);
        autenticarComoTrabajador();
        when(repository.findById(1L)).thenReturn(Optional.of(horario));

        service.eliminar(1L);

        verify(repository, times(1)).delete(horario);
    }

    @Test
    void eliminar_deberiaRechazarSiHorarioNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(999L));
    }

    @Test
    void eliminar_deberiaRechazarAccesoAjeno() {
        Trabajador otro = new Trabajador("Otro", "otro@demo.com", "password");
        otro.setId(99L);
        HorarioDisponibilidad horario = new HorarioDisponibilidad();
        horario.setId(1L);
        horario.setTrabajador(otro);
        autenticarComoTrabajador();
        when(repository.findById(1L)).thenReturn(Optional.of(horario));

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> service.eliminar(1L));
    }
}
