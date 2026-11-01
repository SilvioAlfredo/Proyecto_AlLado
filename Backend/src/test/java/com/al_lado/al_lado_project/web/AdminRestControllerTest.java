package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.admin.AdminRestController;
import com.al_lado.al_lado_project.dto.admin.DashboardMetricsDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.usuario.AdministradorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU-19: Administrador consulta panel con metricas de la plataforma.
 *
 * <p>Pruebas de contrato de {@link AdminRestController}. Valida acceso al
 * dashboard de metricas solo para administradores.</p>
 */
@WebMvcTest(AdminRestController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AdminRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdministradorService administradorService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboardMetrics_deberiaRetornarMetricasParaAdmin() throws Exception {
        DashboardMetricsDTO dto = new DashboardMetricsDTO();
        dto.setTotalIngresos("$100000");
        dto.setMetricContratos(50L);
        dto.setMetricTrabajadores(30L);
        when(administradorService.obtenerMetricasDashboard()).thenReturn(dto);

        mockMvc.perform(get("/api/admin/dashboard/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIngresos").value("$100000"))
                .andExpect(jsonPath("$.metricContratos").value(50))
                .andExpect(jsonPath("$.metricTrabajadores").value(30));
    }

    @Test
    void getDashboardMetrics_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/metrics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void getDashboardMetrics_deberiaRechazarANoAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/metrics"))
                .andExpect(status().isForbidden());
    }
}
