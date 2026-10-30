package com.al_lado.al_lado_project.web;

import com.al_lado.al_lado_project.controller.auth.AuthController;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RF-01 / HU-04 CA-01: validacion del alta de cuentas en la capa web.
 *
 * <p>La longitud minima de la contrasena (8 caracteres) y el formato del email
 * los aplica el Bean Validation sobre {@code RegisterRequestDTO}, que se
 * dispara en el controlador con {@code @Valid}. Por eso el caso vive en la capa
 * web y no en el servicio.</p>
 *
 * <p>Corresponde a TC-LIM-003 del plan de pruebas: 7 caracteres se rechazan y 8
 * se aceptan.</p>
 */
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuthControllerValidationTest {

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
    void registrar_deberiaRechazarContrasenaDeSieteCaracteres() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("Clave12")))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequestDTO.class));
    }

    @Test
    void registrar_deberiaAceptarContrasenaDeOchoCaracteres() throws Exception {
        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(AuthResponseDTO.builder()
                        .token("jwt-generado")
                        .email("nuevo@demo.com")
                        .roles(List.of("ROLE_TRABAJADOR"))
                        .build());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("Clave123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-generado"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_TRABAJADOR"));
    }

    @Test
    void registrar_deberiaRechazarEmailInvalido() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Nuevo Usuario","email":"no-es-un-email",
                                 "password":"Clave123!","telefono":"+549110000999","rol":"TRABAJADOR"}
                                """))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequestDTO.class));
    }

    @Test
    void registrar_deberiaRechazarRolInvalido() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Nuevo Usuario","email":"nuevo@demo.com",
                                 "password":"Clave123!","telefono":"+549110000999","rol":"ADMINISTRADOR"}
                                """))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequestDTO.class));
    }

    @Test
    void registrar_deberiaRechazarTelefonoAusente() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Nuevo Usuario","email":"nuevo@demo.com",
                                 "password":"Clave123!","rol":"TRABAJADOR"}
                                """))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequestDTO.class));
    }

    private String registroJson(String password) {
        return """
                {"nombre":"Nuevo Usuario","email":"nuevo@demo.com","password":"%s",
                 "telefono":"+549110000999","rol":"TRABAJADOR"}
                """.formatted(password);
    }
}
