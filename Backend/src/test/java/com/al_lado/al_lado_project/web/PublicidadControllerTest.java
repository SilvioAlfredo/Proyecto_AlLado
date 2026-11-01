package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.comunidad.PublicidadController;
import com.al_lado.al_lado_project.dto.comunidad.PublicidadDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.comunidad.PublicidadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU-15: Publicar anuncios de comercio en la plataforma.
 *
 * <p>Pruebas de contrato de {@link PublicidadController}. Valida listado,
 * creacion, actualizacion, eliminacion y anuncios vigentes con control de
 * acceso.</p>
 */
@WebMvcTest(PublicidadController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class PublicidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicidadService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private PublicidadDTO dto() {
        PublicidadDTO dto = new PublicidadDTO();
        dto.setId(1L);
        dto.setTitulo("Mi Anuncio");
        dto.setActiva(true);
        dto.setFechaInicio(LocalDate.of(2026, 1, 1));
        dto.setFechaFin(LocalDate.of(2026, 12, 31));
        return dto;
    }

    @Test
    void obtenerTodas_deberiaRetornarListaDePublicidades() throws Exception {
        when(service.obtenerTodas()).thenReturn(List.of(dto()));

        mockMvc.perform(get("/publicidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Mi Anuncio"));
    }

    @Test
    void obtenerVigentes_deberiaRetornarAnunciosVigentes() throws Exception {
        when(service.obtenerVigentes()).thenReturn(List.of(dto()));

        mockMvc.perform(get("/publicidades/vigentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Mi Anuncio"));
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void guardar_deberiaCrearPublicidad() throws Exception {
        when(service.guardar(any())).thenReturn(dto());

        mockMvc.perform(post("/publicidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Mi Anuncio\",\"fechaInicio\":\"2026-01-01\",\"fechaFin\":\"2026-12-31\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Mi Anuncio"));
    }

    @Test
    void guardar_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(post("/publicidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void guardar_deberiaRechazarATrabajador() throws Exception {
        mockMvc.perform(post("/publicidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void eliminar_deberiaEliminarPublicidad() throws Exception {
        mockMvc.perform(delete("/publicidades/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(delete("/publicidades/1"))
                .andExpect(status().isUnauthorized());
    }
}
