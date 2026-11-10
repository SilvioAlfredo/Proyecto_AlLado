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

import com.al_lado.al_lado_project.controller.comunidad.NotificacionController;
import com.al_lado.al_lado_project.service.comunidad.NotificacionService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(NotificacionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionService notificacionService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeListarNotificaciones() throws Exception {
        mockMvc.perform(get("/notificaciones/usuario/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeContarNoLeidas() throws Exception {
        mockMvc.perform(get("/notificaciones/usuario/1/no-leidas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeMarcarComoLeida() throws Exception {
        mockMvc.perform(patch("/notificaciones/1/leer")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeMarcarTodasComoLeidas() throws Exception {
        mockMvc.perform(patch("/notificaciones/usuario/1/leer-todas")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
