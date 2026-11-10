package com.al_lado.al_lado_project.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.al_lado.al_lado_project.controller.admin.AdminRestController;
import com.al_lado.al_lado_project.service.usuario.AdministradorService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(AdminRestController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AdminRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdministradorService administradorService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimo_noPuedeAccederDashboard() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/metrics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_noPuedeAccederDashboard() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/metrics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_noPuedeAccederDashboard() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/metrics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeAccederDashboard() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/metrics"))
                .andExpect(status().isOk());
    }
}
