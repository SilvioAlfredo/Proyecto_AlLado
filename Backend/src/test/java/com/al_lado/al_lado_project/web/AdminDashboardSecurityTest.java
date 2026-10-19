package com.al_lado.al_lado_project.web;
import com.al_lado.al_lado_project.controller.admin.AdminRestController;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.*;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(AdminRestController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AdminDashboardSecurityTest {
    @Autowired MockMvc mvc;
    @MockBean com.al_lado.al_lado_project.service.usuario.AdministradorService administrador;
    @MockBean JwtUtils jwtUtils;
    @MockBean UserDetailsServiceImpl userDetailsService;
    @MockBean TokenRevocadoRepository tokenRevocadoRepository;
    @Test void anonimoSinMetricas() throws Exception {
        mvc.perform(get("/api/admin/dashboard/metrics")).andExpect(status().isUnauthorized());
        verifyNoInteractions(administrador);
    }
    @Test @WithMockUser(roles="TRABAJADOR") void trabajadorSinMetricas() throws Exception {
        mvc.perform(get("/api/admin/dashboard/metrics")).andExpect(status().isForbidden());
        verifyNoInteractions(administrador);
    }
    @Test @WithMockUser(roles="EMPLEADOR") void empleadorSinMetricas() throws Exception {
        mvc.perform(get("/api/admin/dashboard/metrics")).andExpect(status().isForbidden());
        verifyNoInteractions(administrador);
    }
    @Test @WithMockUser(roles="ADMIN") void adminObtieneCerosReales() throws Exception {
        var dto = new com.al_lado.al_lado_project.dto.admin.DashboardMetricsDTO();
        dto.setMetricTrabajadores(0L); dto.setMetricContratos(0L); dto.setTrabajosCompletados(0L);
        dto.setTendencias(List.of()); dto.setPostulacionesPorEstado(java.util.Map.of("PENDIENTE",0L));
        when(administrador.obtenerMetricasDashboard()).thenReturn(dto);
        mvc.perform(get("/api/admin/dashboard/metrics")).andExpect(status().isOk())
            .andExpect(jsonPath("$.metricTrabajadores").value(0))
            .andExpect(jsonPath("$.tendencias").isEmpty())
            .andExpect(jsonPath("$.postulacionesPorEstado.PENDIENTE").value(0));
    }
}
