package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.trabajo.ContratoController;
import com.al_lado.al_lado_project.dto.trabajo.ContratoDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.trabajo.ContratoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU-08: Historial de contratos del empleador y del trabajador.
 *
 * <p>Pruebas de contrato de {@link ContratoController}. Valida acceso por rol
 * y retorno de datos.</p>
 */
@WebMvcTest(ContratoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class ContratoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContratoService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private ContratoDTO dto() {
        ContratoDTO dto = new ContratoDTO();
        dto.setId(100L);
        dto.setEstado("COMPLETADA");
        dto.setFechaCreacion(LocalDateTime.of(2026, 1, 15, 10, 0));
        return dto;
    }

    @Test
    void historialEmpleador_deberiaRetornarContratosParaEmpleadorAutenticado() throws Exception {
        when(service.historialEmpleador()).thenReturn(List.of(dto()));

        mockMvc.perform(get("/contratos/historial/empleador"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].estado").value("COMPLETADA"));
    }

    @Test
    void historialEmpleador_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(get("/contratos/historial/empleador"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void historialEmpleador_deberiaRechazarATrabajador() throws Exception {
        mockMvc.perform(get("/contratos/historial/empleador"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void historialTrabajador_deberiaRetornarContratosDelTrabajador() throws Exception {
        when(service.historialTrabajador(anyLong())).thenReturn(List.of(dto()));

        mockMvc.perform(get("/contratos/historial/trabajador/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100));
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void obtenerPorId_deberiaRetornarContratoDetalle() throws Exception {
        when(service.obtenerPorId(100L)).thenReturn(dto());

        mockMvc.perform(get("/contratos/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void obtenerPorId_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(get("/contratos/100"))
                .andExpect(status().isUnauthorized());
    }
}
