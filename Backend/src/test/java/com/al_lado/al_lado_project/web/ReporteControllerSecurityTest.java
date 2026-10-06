package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.comunidad.ReporteController;
import com.al_lado.al_lado_project.dto.comunidad.ReporteDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.comunidad.ReporteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AL-TEST-001 (matriz 403/200 por rol) y AL-TEST-002 (contrato de reportes).
 */
@WebMvcTest(ReporteController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class ReporteControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReporteService service;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimoNoPuedeListarReportes() throws Exception {
        mockMvc.perform(get("/reportes")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajadorNoPuedeListarReportes() throws Exception {
        mockMvc.perform(get("/reportes")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminSiPuedeListarReportes() throws Exception {
        ReporteDTO dto = new ReporteDTO();
        dto.setId(1L);
        dto.setMotivo("Spam");
        when(service.obtenerTodos(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].motivo").value("Spam"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPaginaSegundaPaginaDeReportes() throws Exception {
        when(service.obtenerTodos(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/reportes").param("page", "2").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleadorPuedeCrearReporte() throws Exception {
        ReporteDTO creado = new ReporteDTO();
        creado.setId(5L);
        creado.setEstado("PENDIENTE");
        when(service.crear(any(ReporteDTO.class))).thenReturn(creado);

        mockMvc.perform(post("/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\":\"Spam\",\"descripcion\":\"X\","
                                + "\"reportante\":{\"id\":2}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void crearSinMotivoEs400() throws Exception {
        mockMvc.perform(post("/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"X\",\"reportante\":{\"id\":2}}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPuedeResolver() throws Exception {
        ReporteDTO resuelto = new ReporteDTO();
        resuelto.setId(1L);
        resuelto.setEstado("RESUELTO");
        when(service.resolver(1L, 1L, null)).thenReturn(resuelto);

        mockMvc.perform(put("/reportes/1/resolver").param("moderadorId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RESUELTO"));
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajadorNoPuedeResolver() throws Exception {
        mockMvc.perform(put("/reportes/1/resolver").param("moderadorId", "1"))
                .andExpect(status().isForbidden());
    }
}
