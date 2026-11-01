package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.comunidad.PerfilEmpresarialController;
import com.al_lado.al_lado_project.dto.comunidad.PerfilEmpresarialDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.comunidad.PerfilEmpresarialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU-14: Publicar el perfil de mi comercio y obtener el Sello de Confianza.
 *
 * <p>Pruebas de contrato de {@link PerfilEmpresarialController}. Valida
 * obtener, guardar y verificar perfil empresarial con control de acceso.</p>
 */
@WebMvcTest(PerfilEmpresarialController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class PerfilEmpresarialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PerfilEmpresarialService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private PerfilEmpresarialDTO dto() {
        PerfilEmpresarialDTO dto = new PerfilEmpresarialDTO();
        dto.setId(1L);
        dto.setNombreEmpresa("Mi Comercio");
        dto.setCuit("20-12345678-9");
        return dto;
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void obtenerPorEmpleador_deberiaRetornarPerfil() throws Exception {
        when(service.obtenerPorEmpleador(1L)).thenReturn(dto());

        mockMvc.perform(get("/perfiles-empresariales/empleador/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreEmpresa").value("Mi Comercio"));
    }

    @Test
    void obtenerPorEmpleador_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(get("/perfiles-empresariales/empleador/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void guardar_deberiaCrearOActualizarPerfil() throws Exception {
        when(service.guardar(anyLong(), any())).thenReturn(dto());

        mockMvc.perform(put("/perfiles-empresariales/empleador/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreEmpresa\":\"Mi Comercio\",\"cuit\":\"20-12345678-9\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreEmpresa").value("Mi Comercio"));
    }

    @Test
    void guardar_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(put("/perfiles-empresariales/empleador/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verificar_deberiaMarcarPerfilComoVerificado() throws Exception {
        when(service.verificar(1L, null)).thenReturn(dto());

        mockMvc.perform(put("/perfiles-empresariales/1/verificar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreEmpresa").value("Mi Comercio"));
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void verificar_deberiaRechazarANoAdmin() throws Exception {
        mockMvc.perform(put("/perfiles-empresariales/1/verificar"))
                .andExpect(status().isForbidden());
    }
}
