package com.al_lado.al_lado_project.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.al_lado.al_lado_project.controller.trabajo.OportunidadLaboralController;
import com.al_lado.al_lado_project.service.trabajo.OportunidadLaboralService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(OportunidadLaboralController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class OportunidadLaboralControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OportunidadLaboralService oportunidadLaboralService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleadorAutenticado_noPuedeVerListadoDeOportunidades() throws Exception {
        mockMvc.perform(get("/oportunidades")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_noPuedeVerOportunidadPorId() throws Exception {
        mockMvc.perform(get("/oportunidades/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeVerListadoDeOportunidades() throws Exception {
        when(oportunidadLaboralService.listarOportunidades(any(), any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/oportunidades"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeVerListadoDeOportunidades() throws Exception {
        when(oportunidadLaboralService.listarOportunidades(any(), any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/oportunidades"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeFiltrarOportunidades() throws Exception {
        when(oportunidadLaboralService.listarConFiltros(any(), any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(post("/oportunidades/filtrar")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"filtro\":\"electricidad\"}")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_noPuedeFiltrarOportunidades() throws Exception {
        mockMvc.perform(post("/oportunidades/filtrar")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"filtro\":\"electricidad\"}")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
