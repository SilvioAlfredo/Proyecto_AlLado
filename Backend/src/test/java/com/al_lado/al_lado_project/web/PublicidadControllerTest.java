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

import com.al_lado.al_lado_project.controller.comunidad.PublicidadController;
import com.al_lado.al_lado_project.service.comunidad.PublicidadService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(PublicidadController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class PublicidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicidadService publicidadService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimo_puedeListarPublicidades() throws Exception {
        mockMvc.perform(get("/publicidades"))
                .andExpect(status().isOk());
    }

    @Test
    void anonimo_puedeVerPublicidadesVigentes() throws Exception {
        mockMvc.perform(get("/publicidades/vigentes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_puedeCrearPublicidad() throws Exception {
        mockMvc.perform(post("/publicidades")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Anuncio\",\"contenido\":\"Contenido\",\"fechaInicio\":\"2026-11-01\",\"fechaFin\":\"2026-12-31\",\"anunciante\":{\"id\":1}}")
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeEliminarPublicidad() throws Exception {
        mockMvc.perform(delete("/publicidades/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
