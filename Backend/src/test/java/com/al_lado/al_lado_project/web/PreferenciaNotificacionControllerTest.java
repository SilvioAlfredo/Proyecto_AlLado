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

import com.al_lado.al_lado_project.controller.perfil.PreferenciaNotificacionController;
import com.al_lado.al_lado_project.service.perfil.PreferenciaNotificacionService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(PreferenciaNotificacionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class PreferenciaNotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PreferenciaNotificacionService preferenciaNotificacionService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimo_noPuedeVerPreferenciasDeTrabajador() throws Exception {
        // AL-SEC-006: las preferencias exigen sesión; el anónimo recibe 401.
        mockMvc.perform(get("/preferencias/trabajador/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeGuardarPreferencias() throws Exception {
        mockMvc.perform(put("/preferencias/trabajador/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"notificarEmail\":true,\"notificarPush\":true,\"alertasNuevasOfertas\":true,\"alertasNuevasPostulaciones\":true}")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleador_noPuedeGuardarPreferenciasDeTrabajador() throws Exception {
        mockMvc.perform(put("/preferencias/trabajador/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"notificarEmail\":true}")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
