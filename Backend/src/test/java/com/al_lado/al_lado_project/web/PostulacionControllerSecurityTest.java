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

import com.al_lado.al_lado_project.controller.trabajo.PostulacionController;
import com.al_lado.al_lado_project.service.trabajo.PostulacionService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(PostulacionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class PostulacionControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostulacionService postulacionService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    private static final String POSTULACION_JSON = """
        {"titulo":"Reparacion electrica","contenido":"Tengo experiencia en reparaciones electricas","autor":{"id":3},"oportunidadLaboral":{"id":10},"remuneracionPretendida":18000}
        """;

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_noPuedePostularse() throws Exception {
        mockMvc.perform(post("/postulaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(POSTULACION_JSON)
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleadorAutenticado_noPuedePostularse() throws Exception {
        mockMvc.perform(post("/postulaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(POSTULACION_JSON)
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_noPuedePostularse() throws Exception {
        mockMvc.perform(post("/postulaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(POSTULACION_JSON)
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedePostularse() throws Exception {
        mockMvc.perform(post("/postulaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(POSTULACION_JSON)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void anonimo_noPuedePostularse() throws Exception {
        mockMvc.perform(post("/postulaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(POSTULACION_JSON)
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
