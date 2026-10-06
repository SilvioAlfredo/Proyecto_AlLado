package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.auth.AuthController;
import com.al_lado.al_lado_project.dto.auth.AuthRequestDTO;
import com.al_lado.al_lado_project.dto.auth.AuthResponseDTO;
import com.al_lado.al_lado_project.dto.auth.RegisterRequestDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** AL-TEST-002: contrato de login/registro + 401 sin filtrar internos. */
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void loginOkDevuelveTokenRoles() throws Exception {
        when(authService.login(any(AuthRequestDTO.class))).thenReturn(
                AuthResponseDTO.builder().token("jwt").email("a@mail.com")
                        .roles(List.of("ROLE_TRABAJADOR")).build());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@mail.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_TRABAJADOR"));
    }

    @Test
    void loginFallidoEs401Generico() throws Exception {
        when(authService.login(any(AuthRequestDTO.class)))
                .thenThrow(new BadCredentialsException("AbstractAuthenticationToken expired 12345"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@mail.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales de acceso inválidas."));
    }

    @Test
    void registroConRolInvalidoEs400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"email\":\"a@mail.com\","
                                + "\"password\":\"secret123\",\"telefono\":\"+5491100000000\","
                                + "\"rol\":\"ADMIN\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "a@mail.com", roles = "TRABAJADOR")
    void logoutAutenticadoEs204() throws Exception {
        mockMvc.perform(post("/auth/logout").header("Authorization", "Bearer jwt-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void logoutAnonimoEs401() throws Exception {
        mockMvc.perform(post("/auth/logout").header("Authorization", "Bearer jwt-1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registroValidoLlamaAlServicio() throws Exception {
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(
                AuthResponseDTO.builder().token("jwt").email("a@mail.com")
                        .roles(List.of("ROLE_EMPLEADOR")).build());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"email\":\"a@mail.com\","
                                + "\"password\":\"secret123\",\"telefono\":\"+5491100000000\","
                                + "\"rol\":\"EMPLEADOR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"));
    }
}
