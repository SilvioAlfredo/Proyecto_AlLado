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

import com.al_lado.al_lado_project.controller.usuario.EmpleadorController;
import com.al_lado.al_lado_project.service.usuario.EmpleadorService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(EmpleadorController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class EmpleadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadorService empleadorService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimo_puedeListarEmpleadores() throws Exception {
        mockMvc.perform(get("/empleadores"))
                .andExpect(status().isOk());
    }

    @Test
    void anonimo_puedeVerEmpleadorPorId() throws Exception {
        mockMvc.perform(get("/empleadores/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_puedeActualizarSuPerfil() throws Exception {
        mockMvc.perform(put("/empleadores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Maria\",\"email\":\"maria@test.com\",\"telefono\":\"+5491100000000\"}")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeEliminarEmpleador() throws Exception {
        mockMvc.perform(delete("/empleadores/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
