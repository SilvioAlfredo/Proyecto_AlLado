package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.comunidad.DonacionController;
import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.comunidad.DonacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU-06 CA-02 y HU-16: Verificacion de transferencias y donaciones.
 *
 * <p>Pruebas de contrato de {@link DonacionController}. Valida crear,
 * verificar transferencia y listar donaciones con control de acceso.</p>
 */
@WebMvcTest(DonacionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class DonacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DonacionService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private DonacionDTO dto() {
        DonacionDTO dto = new DonacionDTO();
        dto.setId(1L);
        dto.setMonto(15000.0);
        dto.setAsunto("APORTE PRO");
        dto.setEstado("PENDIENTE");
        return dto;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTodas_deberiaRetornarListaDeDonaciones() throws Exception {
        when(service.obtenerTodas(any())).thenReturn(
                new org.springframework.data.domain.PageImpl<>(List.of(dto()), org.springframework.data.domain.PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/donaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].monto").value(15000.0));
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void obtenerPorUsuario_deberiaRetornarDonacionesDelUsuario() throws Exception {
        when(service.obtenerPorUsuario(1L, any())).thenReturn(
                new org.springframework.data.domain.PageImpl<>(List.of(dto()), org.springframework.data.domain.PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/donaciones/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].asunto").value("APORTE PRO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verificarTransferencia_deberiaVerificarDonacion() throws Exception {
        when(service.verificarTransferencia(1L, "APORTE PRO", new BigDecimal("15000.0"))).thenReturn(dto());

        mockMvc.perform(post("/donaciones/1/verificar")
                        .param("asunto", "APORTE PRO")
                        .param("monto", "15000.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monto").value(15000.0));
    }

    @Test
    void verificarTransferencia_deberiaRechazarANonAdmin() throws Exception {
        mockMvc.perform(post("/donaciones/1/verificar")
                        .param("asunto", "APORTE PRO")
                        .param("monto", "15000.00"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void crear_deberiaCrearDonacion() throws Exception {
        when(service.crear(any())).thenReturn(dto());

        mockMvc.perform(post("/donaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monto\":15000.0,\"asunto\":\"APORTE PRO\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void crear_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(post("/donaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
