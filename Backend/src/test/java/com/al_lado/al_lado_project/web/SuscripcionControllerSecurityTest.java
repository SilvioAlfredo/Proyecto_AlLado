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

import com.al_lado.al_lado_project.controller.usuario.SuscripcionController;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(SuscripcionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class SuscripcionControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SuscripcionService suscripcionService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimo_noPuedeListarSuscripciones() throws Exception {
        mockMvc.perform(get("/suscripciones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_noPuedeListarSuscripciones() throws Exception {
        mockMvc.perform(get("/suscripciones"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeListarSuscripciones() throws Exception {
        mockMvc.perform(get("/suscripciones"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeCancelarSuscripcion() throws Exception {
        mockMvc.perform(put("/suscripciones/cancelar/1")
                .with(csrf()))
                .andExpect(status().isOk());
    }
}
