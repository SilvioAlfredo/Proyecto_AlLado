package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.trabajo.PostulacionController;
import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.trabajo.PostulacionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** AL-TEST-001 (roles en postulaciones) y AL-TEST-002 (contrato de creación). */
@WebMvcTest(PostulacionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class PostulacionControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostulacionService service;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private static final String POSTULACION_VALIDA = "{\"titulo\":\"Ayuda\",\"contenido\":\"C\","
            + "\"autor\":{\"id\":1},\"oportunidadLaboral\":{\"id\":10}}";

    @Test
    void anonimoNoPuedePostular() throws Exception {
        mockMvc.perform(post("/postulaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(POSTULACION_VALIDA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajadorSiPuedePostular() throws Exception {
        PostulacionDTO creada = new PostulacionDTO();
        creada.setId(3L);
        creada.setEstado("PENDIENTE");
        when(service.crearPostulacion(any(PostulacionDTO.class))).thenReturn(creada);

        mockMvc.perform(post("/postulaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(POSTULACION_VALIDA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleadorNoPuedePostular() throws Exception {
        mockMvc.perform(post("/postulaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(POSTULACION_VALIDA))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void crearSinAutorEs400() throws Exception {
        mockMvc.perform(post("/postulaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Ayuda\",\"contenido\":\"C\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleadorSiPuedeSeleccionar() throws Exception {
        PostulacionDTO dto = new PostulacionDTO();
        dto.setEstado("SELECCIONADA");
        when(service.seleccionarPostulacion(3L)).thenReturn(dto);

        mockMvc.perform(patch("/postulaciones/3/seleccionar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("SELECCIONADA"));
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajadorNoPuedeSeleccionar() throws Exception {
        mockMvc.perform(patch("/postulaciones/3/seleccionar"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void empleadorSiPuedeCompletar() throws Exception {
        PostulacionDTO dto = new PostulacionDTO();
        dto.setEstado("COMPLETADA");
        when(service.completarPostulacion(3L)).thenReturn(dto);

        mockMvc.perform(patch("/postulaciones/3/completar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }
}
