package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.trabajo.OportunidadLaboralController;
import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.trabajo.OportunidadLaboralService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** AL-TEST-002: contrato público y protegido de oportunidades. */
@WebMvcTest(OportunidadLaboralController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class OportunidadControllerContractTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OportunidadLaboralService service;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void listadoPublicoSinToken() throws Exception {
        OportunidadLaboralDTO dto = new OportunidadLaboralDTO();
        dto.setId(1L);
        dto.setTitulo("Albañil");
        when(service.listarOportunidades(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/oportunidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Albañil"));
    }

    @Test
    void anonimoNoPuedePublicar() throws Exception {
        mockMvc.perform(post("/oportunidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"T\",\"contenido\":\"C\",\"autor\":{\"id\":5}}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajadorNoPuedePublicar() throws Exception {
        mockMvc.perform(post("/oportunidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"T\",\"contenido\":\"C\",\"autor\":{\"id\":5}}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleadorSiPuedePublicar() throws Exception {
        OportunidadLaboralDTO creada = new OportunidadLaboralDTO();
        creada.setId(7L);
        creada.setActiva(true);
        when(service.guardar(any(OportunidadLaboralDTO.class))).thenReturn(creada);

        mockMvc.perform(post("/oportunidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"T\",\"contenido\":\"C\",\"autor\":{\"id\":5}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.activa").value(true));
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void filtrarRequiereSesionYDevuelveLista() throws Exception {
        when(service.listarConFiltros(any())).thenReturn(List.of());

        mockMvc.perform(post("/oportunidades/filtrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void violacionDeIntegridadEs409Generico() throws Exception {
        when(service.guardar(any(OportunidadLaboralDTO.class)))
                .thenThrow(new DataIntegrityViolationException("could not execute statement"));

        mockMvc.perform(post("/oportunidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"T\",\"contenido\":\"C\",\"autor\":{\"id\":5}}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La operación viola una restricción de integridad de datos."));
    }
}
