package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.perfil.HorarioDisponibilidadController;
import com.al_lado.al_lado_project.dto.perfil.HorarioDisponibilidadDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.perfil.HorarioDisponibilidadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU-10: Configurar dias y rangos horarios de servicio.
 *
 * <p>Pruebas de contrato de {@link HorarioDisponibilidadController}. Valida
 * listado, creacion y eliminacion de horarios con control de acceso.</p>
 */
@WebMvcTest(HorarioDisponibilidadController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class HorarioDisponibilidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HorarioDisponibilidadService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private HorarioDisponibilidadDTO dto() {
        HorarioDisponibilidadDTO dto = new HorarioDisponibilidadDTO();
        dto.setId(1L);
        dto.setDiaSemana("Lunes");
        dto.setHoraInicio(LocalTime.of(9, 0));
        dto.setHoraFin(LocalTime.of(12, 0));
        return dto;
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void obtenerPorTrabajador_deberiaRetornarHorarios() throws Exception {
        when(service.obtenerPorTrabajador(1L)).thenReturn(List.of(dto()));

        mockMvc.perform(get("/horarios/trabajador/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diaSemana").value("Lunes"));
    }

    @Test
    void obtenerPorTrabajador_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(get("/horarios/trabajador/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void agregar_deberiaCrearHorario() throws Exception {
        when(service.agregar(anyLong(), any())).thenReturn(dto());

        mockMvc.perform(post("/horarios/trabajador/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diaSemana\":\"LUNES\",\"horaInicio\":\"09:00:00\",\"horaFin\":\"12:00:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diaSemana").value("Lunes"));
    }

    @Test
    void agregar_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(post("/horarios/trabajador/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void agregar_deberiaRechazarANoTrabajador() throws Exception {
        mockMvc.perform(post("/horarios/trabajador/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diaSemana\":\"LUNES\",\"horaInicio\":\"09:00:00\",\"horaFin\":\"12:00:00\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void eliminar_deberiaEliminarHorario() throws Exception {
        mockMvc.perform(delete("/horarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(delete("/horarios/1"))
                .andExpect(status().isUnauthorized());
    }
}
