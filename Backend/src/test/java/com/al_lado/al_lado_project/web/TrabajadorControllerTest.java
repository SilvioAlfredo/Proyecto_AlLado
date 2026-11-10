package com.al_lado.al_lado_project.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.al_lado.al_lado_project.controller.usuario.TrabajadorController;
import com.al_lado.al_lado_project.service.trabajo.PostulacionService;
import com.al_lado.al_lado_project.service.usuario.TrabajadorService;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@WebMvcTest(TrabajadorController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class TrabajadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrabajadorService trabajadorService;

    @MockBean
    private PostulacionService postulacionService;

    @MockBean
    private com.al_lado.al_lado_project.security.jwt.JwtUtils jwtUtils;

    @MockBean
    private com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimo_puedeListarTrabajadores() throws Exception {
        mockMvc.perform(get("/trabajadores"))
                .andExpect(status().isOk());
    }

    @Test
    void anonimo_puedeVerTrabajadorPorId() throws Exception {
        mockMvc.perform(get("/trabajadores/1"))
                .andExpect(status().isOk());
    }

    @Test
    void anonimo_puedeFiltrarTrabajadores() throws Exception {
        mockMvc.perform(get("/trabajadores/filtrar")
                .param("etiqueta", "Electricidad")
                .param("sector", "Centro"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajador_puedeActualizarSuPerfil() throws Exception {
        mockMvc.perform(put("/trabajadores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Juan\",\"email\":\"juan@test.com\",\"telefono\":\"+5491100000000\",\"biografia\":\"Desarrollador\"}")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeEliminarTrabajador() throws Exception {
        mockMvc.perform(delete("/trabajadores/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
