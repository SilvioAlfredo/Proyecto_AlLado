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

import com.al_lado.al_lado_project.controller.comunidad.PerfilEmpresarialController;
import com.al_lado.al_lado_project.service.comunidad.PerfilEmpresarialService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(PerfilEmpresarialController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class PerfilEmpresarialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PerfilEmpresarialService perfilEmpresarialService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimo_puedeVerPerfilEmpresarial() throws Exception {
        mockMvc.perform(get("/perfiles-empresariales/empleador/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_puedeGuardarPerfilEmpresarial() throws Exception {
        mockMvc.perform(put("/perfiles-empresariales/empleador/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombreEmpresa\":\"Mi Empresa\",\"cuit\":\"20-12345678-9\"}")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeVerificarPerfilEmpresarial() throws Exception {
        mockMvc.perform(put("/perfiles-empresariales/1/verificar")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_noPuedeVerificarPerfilEmpresarial() throws Exception {
        mockMvc.perform(put("/perfiles-empresariales/1/verificar")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
