package com.al_lado.al_lado_project.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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

import com.al_lado.al_lado_project.controller.comunidad.ValoracionController;
import com.al_lado.al_lado_project.service.comunidad.ValoracionService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(ValoracionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class ValoracionControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ValoracionService valoracionService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimo_noPuedeCrearValoracion() throws Exception {
        mockMvc.perform(post("/valoraciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":5,\"comentario\":\"Regular\",\"autor\":{\"id\":1},\"trabajador\":{\"id\":2}}")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_noPuedeCrearValoracion() throws Exception {
        mockMvc.perform(post("/valoraciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":5,\"comentario\":\"Regular\",\"autor\":{\"id\":1},\"trabajador\":{\"id\":2}}")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_puedeCrearValoracion() throws Exception {
        mockMvc.perform(post("/valoraciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":5,\"comentario\":\"Bien\",\"autor\":{\"id\":1},\"trabajador\":{\"id\":2}}")
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeEliminarValoracion() throws Exception {
        mockMvc.perform(delete("/valoraciones/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
