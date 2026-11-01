package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.comunidad.NotificacionController;
import com.al_lado.al_lado_project.dto.comunidad.NotificacionDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.comunidad.NotificacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU-09: Trabajador recibe notificaciones dentro de la aplicación.
 *
 * <p>Pruebas de contrato de {@link NotificacionController}. Valida listado,
 * contador de no leidas, marcar como leida y marcar todas como leidas.</p>
 */
@WebMvcTest(NotificacionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private NotificacionDTO dto() {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(1L);
        dto.setTitulo("Nueva postulación recibida");
        dto.setMensaje("Ana se postuló a tu oferta");
        dto.setTipo("POSTULACION_RECIBIDA");
        return dto;
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void listarPorUsuario_deberiaRetornarNotificaciones() throws Exception {
        when(service.listarPorUsuario(1L)).thenReturn(List.of(dto()));

        mockMvc.perform(get("/notificaciones/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Nueva postulación recibida"));
    }

    @Test
    void listarPorUsuario_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(get("/notificaciones/usuario/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void contarNoLeidas_deberiaRetornarContador() throws Exception {
        when(service.contarNoLeidas(1L)).thenReturn(3L);

        mockMvc.perform(get("/notificaciones/usuario/1/no-leidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noLeidas").value(3));
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void marcarLeida_deberiaMarcarNotificacionComoLeida() throws Exception {
        when(service.marcarLeida(1L)).thenReturn(dto());

        mockMvc.perform(patch("/notificaciones/1/leer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void marcarLeida_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(patch("/notificaciones/1/leer"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void marcarTodasLeidas_deberiaMarcarTodas() throws Exception {
        mockMvc.perform(patch("/notificaciones/usuario/1/leer-todas"))
                .andExpect(status().isNoContent());
    }

    @Test
    void marcarTodasLeidas_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(patch("/notificaciones/usuario/1/leer-todas"))
                .andExpect(status().isUnauthorized());
    }
}
