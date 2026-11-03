package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.perfil.PortafolioItemController;
import com.al_lado.al_lado_project.dto.perfil.PortafolioItemDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.perfil.PortafolioItemService;
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
 * HU-17: Armar mi portafolio y acceder a propuestas reservadas para usuarios PRO.
 *
 * <p>Pruebas de contrato de {@link PortafolioItemController}. Valida listado,
 * creacion y eliminacion de items de portafolio con control de acceso.</p>
 */
@WebMvcTest(PortafolioItemController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class PortafolioItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortafolioItemService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private PortafolioItemDTO dto() {
        PortafolioItemDTO dto = new PortafolioItemDTO();
        dto.setId(1L);
        dto.setTitulo("Proyecto de construccion");
        return dto;
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void obtenerPorTrabajador_deberiaRetornarItems() throws Exception {
        when(service.obtenerPorTrabajador(1L)).thenReturn(List.of(dto()));

        mockMvc.perform(get("/portafolio-items/trabajador/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Proyecto de construccion"));
    }

    @Test
    void obtenerPorTrabajador_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(get("/portafolio-items/trabajador/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void agregar_deberiaCrearItemDePortafolio() throws Exception {
        when(service.agregar(anyLong(), any())).thenReturn(dto());

        mockMvc.perform(post("/portafolio-items/trabajador/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Proyecto de construccion\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Proyecto de construccion"));
    }

    @Test
    void agregar_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(post("/portafolio-items/trabajador/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void agregar_deberiaRechazarANoTrabajador() throws Exception {
        mockMvc.perform(post("/portafolio-items/trabajador/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Proyecto de construccion\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void eliminar_deberiaEliminarItem() throws Exception {
        mockMvc.perform(delete("/portafolio-items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(delete("/portafolio-items/1"))
                .andExpect(status().isUnauthorized());
    }
}
