package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.comunidad.ValoracionController;
import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.comunidad.ValoracionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
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
 * HU-06: Calificar con estrellas y dejar una reseña sobre el servicio recibido.
 *
 * <p>Pruebas de contrato de {@link ValoracionController}. Valida listado de
 * valoraciones, promedio, creacion y eliminacion con control de acceso.</p>
 */
@WebMvcTest(ValoracionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class ValoracionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ValoracionService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private ValoracionDTO dto() {
        ValoracionDTO dto = new ValoracionDTO();
        dto.setId(1L);
        dto.setPuntuacion(5);
        dto.setComentario("Excelente servicio");
        return dto;
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void obtenerPorTrabajador_deberiaRetornarValoraciones() throws Exception {
        when(service.obtenerPorTrabajador(1L, any())).thenReturn(
                new org.springframework.data.domain.PageImpl<>(List.of(dto()), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/valoraciones/trabajador/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].puntuacion").value(5));
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void obtenerPromedioPorTrabajador_deberiaRetornarPromedio() throws Exception {
        when(service.obtenerPromedioPorTrabajador(1L)).thenReturn(4.5);

        mockMvc.perform(get("/valoraciones/trabajador/1/promedio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(4.5));
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void crear_deberiaCrearValoracion() throws Exception {
        when(service.crear(any())).thenReturn(dto());

        mockMvc.perform(post("/valoraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"puntuacion\":5,\"comentario\":\"Excelente servicio\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.puntuacion").value(5));
    }

    @Test
    void crear_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(post("/valoraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void crear_deberiaRechazarATrabajador() throws Exception {
        mockMvc.perform(post("/valoraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_deberiaEliminarValoracion() throws Exception {
        mockMvc.perform(delete("/valoraciones/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(delete("/valoraciones/1"))
                .andExpect(status().isUnauthorized());
    }
}
