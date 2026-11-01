package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.usuario.SuscripcionController;
import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU-16: Adquirir el nivel PRO realizando un aporte por transferencia.
 *
 * <p>Pruebas de contrato de {@link SuscripcionController}. Valida CRUD de
 * suscripciones con control de acceso por rol.</p>
 */
@WebMvcTest(SuscripcionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class SuscripcionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SuscripcionService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private SuscripcionDTO dto() {
        SuscripcionDTO dto = new SuscripcionDTO();
        dto.setId(1L);
        dto.setNivel("BASICO");
        dto.setActiva(true);
        return dto;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTodas_deberiaRetornarTodasLasSuscripciones() throws Exception {
        when(service.obtenerTodas()).thenReturn(List.of(dto()));

        mockMvc.perform(get("/suscripciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nivel").value("BASICO"));
    }

    @Test
    void obtenerTodas_deberiaRechazarANonAdmin() throws Exception {
        mockMvc.perform(get("/suscripciones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void obtenerPorUsuario_deberiaRetornarSuscripcionDelUsuario() throws Exception {
        when(service.obtenerPorUsuario(1L)).thenReturn(dto());

        mockMvc.perform(get("/suscripciones/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivel").value("BASICO"));
    }

    @Test
    void crear_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(post("/suscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_deberiaEliminarSuscripcion() throws Exception {
        mockMvc.perform(delete("/suscripciones/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_deberiaRechazarANonAdmin() throws Exception {
        mockMvc.perform(delete("/suscripciones/1"))
                .andExpect(status().isUnauthorized());
    }
}
