package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.comunidad.ComunicadoController;
import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.comunidad.ComunicadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU-20: Leer los comunicados institucionales de la plataforma.
 *
 * <p>Pruebas de contrato de {@link ComunicadoController}. Valida listado
 * publico, filtrado por alcance y creacion restringida a admin.</p>
 */
@WebMvcTest(ComunicadoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class ComunicadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComunicadoService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    private ComunicadoDTO dto() {
        ComunicadoDTO dto = new ComunicadoDTO();
        dto.setId(1L);
        dto.setTitulo("Aviso importante");
        dto.setContenido("Nuevas funciones disponibles");
        return dto;
    }

    @Test
    void obtenerTodos_deberiaRetornarListaDeComunicados() throws Exception {
        when(service.obtenerTodos(any())).thenReturn(
                new org.springframework.data.domain.PageImpl<>(List.of(dto()), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/comunicados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Aviso importante"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_deberiaCrearComunicado() throws Exception {
        when(service.crear(any())).thenReturn(dto());

        mockMvc.perform(post("/comunicados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Aviso importante\",\"contenido\":\"Nuevas funciones\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Aviso importante"));
    }

    @Test
    void crear_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(post("/comunicados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLEADOR")
    void crear_deberiaRechazarANoAdmin() throws Exception {
        mockMvc.perform(post("/comunicados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_deberiaEliminarComunicado() throws Exception {
        mockMvc.perform(delete("/comunicados/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_deberiaRechazarANonAutenticado() throws Exception {
        mockMvc.perform(delete("/comunicados/1"))
                .andExpect(status().isUnauthorized());
    }
}
