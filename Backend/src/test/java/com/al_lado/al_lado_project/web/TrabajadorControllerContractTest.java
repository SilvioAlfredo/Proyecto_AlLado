package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.usuario.TrabajadorController;
import com.al_lado.al_lado_project.dto.usuario.TrabajadorDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.trabajo.PostulacionService;
import com.al_lado.al_lado_project.service.usuario.TrabajadorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** AL-TEST-002: contrato de lectura y actualización de perfiles. */
@WebMvcTest(TrabajadorController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class TrabajadorControllerContractTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private TrabajadorService trabajadorService;
    @MockBean private PostulacionService postulacionService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void perfilDevuelveCamposPublicosEsperados() throws Exception {
        TrabajadorDTO dto = new TrabajadorDTO();
        dto.setId(12L);
        dto.setNombre("Ana");
        dto.setBiografia("Carpintera");
        dto.setSector("Centro");
        when(trabajadorService.obtenerPorId(12L)).thenReturn(dto);

        mockMvc.perform(get("/trabajadores/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.biografia").value("Carpintera"))
                .andExpect(jsonPath("$.sector").value("Centro"));
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void actualizarPerfilConJsonValidoDevuelve200() throws Exception {
        TrabajadorDTO dto = new TrabajadorDTO();
        dto.setId(12L);
        dto.setBiografia("Nueva bio");
        when(trabajadorService.guardar(any(TrabajadorDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/trabajadores/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"email\":\"ana@example.com\","
                                + "\"telefono\":\"+5491100000000\",\"biografia\":\"Nueva bio\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.biografia").value("Nueva bio"));
    }
}
