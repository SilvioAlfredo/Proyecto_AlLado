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

import com.al_lado.al_lado_project.controller.perfil.PortafolioItemController;
import com.al_lado.al_lado_project.service.perfil.PortafolioItemService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(PortafolioItemController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class PortafolioItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortafolioItemService portafolioItemService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimo_puedeVerPortafolioDeTrabajador() throws Exception {
        mockMvc.perform(get("/portafolio-items/trabajador/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeAgregarItemPortafolio() throws Exception {
        mockMvc.perform(post("/portafolio-items/trabajador/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Proyecto\",\"descripcion\":\"Descripcion\",\"enlace\":\"http://ejemplo.com\"}")
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeEliminarItemPortafolio() throws Exception {
        mockMvc.perform(delete("/portafolio-items/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_noPuedeAgregarItemPortafolio() throws Exception {
        mockMvc.perform(post("/portafolio-items/trabajador/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Proyecto\",\"descripcion\":\"Descripcion\"}")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
