package com.al_lado.al_lado_project.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.al_lado.al_lado_project.controller.perfil.HorarioDisponibilidadController;
import com.al_lado.al_lado_project.service.perfil.HorarioDisponibilidadService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(HorarioDisponibilidadController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class HorarioDisponibilidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HorarioDisponibilidadService horarioDisponibilidadService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimo_puedeVerHorariosDeTrabajador() throws Exception {
        mockMvc.perform(get("/horarios/trabajador/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeAgregarHorario() throws Exception {
        mockMvc.perform(post("/horarios/trabajador/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"diaSemana\":\"LUNES\",\"horaInicio\":\"08:00\",\"horaFin\":\"12:00\"}")
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeEliminarHorario() throws Exception {
        mockMvc.perform(delete("/horarios/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_noPuedeAgregarHorario() throws Exception {
        mockMvc.perform(post("/horarios/trabajador/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"diaSemana\":\"LUNES\",\"horaInicio\":\"08:00\",\"horaFin\":\"12:00\"}")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
