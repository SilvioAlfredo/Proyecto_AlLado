package com.al_lado.al_lado_project.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.al_lado.al_lado_project.controller.comunidad.ComunicadoController;
import com.al_lado.al_lado_project.service.comunidad.ComunicadoService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(ComunicadoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class ComunicadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComunicadoService comunicadoService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimo_puedeListarComunicados() throws Exception {
        when(comunicadoService.obtenerTodos(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/comunicados"))
                .andExpect(status().isOk());
    }

    @Test
    void anonimo_puedeFiltrarComunicadosPorAlcance() throws Exception {
        when(comunicadoService.obtenerPorAlcance(anyString(), any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/comunicados/alcance/GENERAL"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeCrearComunicado() throws Exception {
        mockMvc.perform(post("/comunicados")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Comunicado test\",\"contenido\":\"Contenido\",\"alcance\":\"GENERAL\",\"emisor\":{\"id\":1}}")
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_noPuedeCrearComunicado() throws Exception {
        mockMvc.perform(post("/comunicados")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Comunicado test\",\"contenido\":\"Contenido\",\"alcance\":\"GENERAL\",\"emisor\":{\"id\":1}}")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
