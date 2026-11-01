package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.comunidad.FavoritoController;
import com.al_lado.al_lado_project.dto.comunidad.FavoritoDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.comunidad.FavoritoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU-11: Empleador almacena favoritos de trabajadores.
 *
 * <p>Pruebas de contrato de {@link FavoritoController}. Valida listado,
 * agregar y quitar favoritos con control de acceso.</p>
 */
@WebMvcTest(FavoritoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class FavoritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoritoService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private FavoritoDTO dto() {
        FavoritoDTO dto = new FavoritoDTO();
        dto.setId(10L);
        return dto;
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void obtenerPorEmpleador_deberiaRetornarFavoritos() throws Exception {
        when(service.obtenerPorEmpleador(1L)).thenReturn(List.of(dto()));

        mockMvc.perform(get("/favoritos/empleador/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void obtenerPorEmpleador_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(get("/favoritos/empleador/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void agregar_deberiaCrearFavorito() throws Exception {
        when(service.agregar(1L, 2L)).thenReturn(dto());

        mockMvc.perform(post("/favoritos/empleador/1/trabajador/2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void agregar_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(post("/favoritos/empleador/1/trabajador/2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void agregar_deberiaRechazarATrabajador() throws Exception {
        mockMvc.perform(post("/favoritos/empleador/1/trabajador/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void quitar_deberiaEliminarFavorito() throws Exception {
        mockMvc.perform(delete("/favoritos/empleador/1/trabajador/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void quitar_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(delete("/favoritos/empleador/1/trabajador/2"))
                .andExpect(status().isUnauthorized());
    }
}
