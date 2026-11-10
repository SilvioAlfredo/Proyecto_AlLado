package com.al_lado.al_lado_project.service.perfil.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
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

import com.al_lado.al_lado_project.dto.perfil.HorarioDisponibilidadDTO;
import com.al_lado.al_lado_project.entity.perfil.HorarioDisponibilidad;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.mappers.perfil.HorarioDisponibilidadMapper;
import com.al_lado.al_lado_project.repository.perfil.HorarioDisponibilidadRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.service.perfil.HorarioDisponibilidadService;

@ExtendWith(MockitoExtension.class)
class HorarioDisponibilidadServiceImplTest {

    @Mock private HorarioDisponibilidadRepository horarioRepository;
    @Mock private TrabajadorRepository trabajadorRepository;
    private final HorarioDisponibilidadMapper mapper = new HorarioDisponibilidadMapper();
    private HorarioDisponibilidadService service;

    private Trabajador trabajador;
    private HorarioDisponibilidad horario;

    @BeforeEach
    void setUp() {
        service = new HorarioDisponibilidadServiceImpl(horarioRepository, trabajadorRepository, mapper);

        // Las operaciones exigen titularidad: autenticar como el trabajador 1
        SecurityContextHolder.clearContext();
        Trabajador sesion = new Trabajador();
        sesion.setId(1L);
        sesion.setEmail("trabajador@test.com");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(sesion, null, sesion.getAuthorities()));

        trabajador = new Trabajador();
        trabajador.setId(1L);
        trabajador.setNombre("Trabajador Test");

        horario = new HorarioDisponibilidad();
        horario.setId(1L);
        horario.setTrabajador(trabajador);
        horario.setDiaSemana("LUNES");
        horario.setHoraInicio(LocalTime.of(8, 0));
        horario.setHoraFin(LocalTime.of(12, 0));
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void agregar_deberiaCrearHorarioCuandoDatosSonValidos() {
        // Dado
        when(horarioRepository.findByTrabajadorIdAndDiaSemanaIgnoreCase(1L, "LUNES"))
                .thenReturn(List.of());
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(horarioRepository.save(any(HorarioDisponibilidad.class))).thenReturn(horario);

        // Cuando
        HorarioDisponibilidadDTO dto = new HorarioDisponibilidadDTO();
        dto.setDiaSemana("LUNES");
        dto.setHoraInicio(LocalTime.of(8, 0));
        dto.setHoraFin(LocalTime.of(12, 0));
        HorarioDisponibilidadDTO resultado = service.agregar(1L, dto);

        // Entonces
        assertNotNull(resultado);
    }

    @Test
    void agregar_deberiaRechazarCuandoHorarioSeSolapa() {
        // Dado: ya existe un horario 08:00-12:00 el LUNES
        when(horarioRepository.findByTrabajadorIdAndDiaSemanaIgnoreCase(1L, "LUNES"))
                .thenReturn(List.of(horario));

        // Cuando
        HorarioDisponibilidadDTO dto = new HorarioDisponibilidadDTO();
        dto.setDiaSemana("LUNES");
        dto.setHoraInicio(LocalTime.of(10, 0));
        dto.setHoraFin(LocalTime.of(14, 0));

        // Entonces
        assertThrows(BusinessRuleException.class, () -> service.agregar(1L, dto));
    }

    @Test
    void eliminar_deberiaEliminarCuandoExiste() {
        // Dado
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));

        // Cuando
        service.eliminar(1L);

        // Entonces
        verify(horarioRepository, times(1)).delete(horario);
    }
}
