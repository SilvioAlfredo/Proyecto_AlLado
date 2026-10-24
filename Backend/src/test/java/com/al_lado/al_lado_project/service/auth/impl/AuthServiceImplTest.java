package com.al_lado.al_lado_project.service.auth.impl;

import com.al_lado.al_lado_project.dto.auth.AuthRequestDTO;
import com.al_lado.al_lado_project.dto.auth.AuthResponseDTO;
import com.al_lado.al_lado_project.dto.auth.RegisterRequestDTO;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RF-01 / HU-04 CA-01: reglas del registro y del inicio de sesion.
 *
 * <p>El alta exige email unico, rol valido (TRABAJADOR o EMPLEADOR) y guarda la
 * contrasena codificada. El inicio de sesion delega la verificacion de
 * credenciales en el {@code AuthenticationManager} y devuelve el token junto con
 * los roles del usuario.</p>
 *
 * <p>La longitud minima de la contrasena (8 caracteres) la aplica el Bean
 * Validation del DTO en la capa web; por eso se verifica en
 * {@code web/AuthControllerValidationTest}.</p>
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private EmpleadorRepository empleadorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenRevocadoRepository tokenRevocadoRepository;

    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AuthServiceImpl(
                authenticationManager,
                jwtUtils,
                usuarioRepository,
                trabajadorRepository,
                empleadorRepository,
                passwordEncoder,
                tokenRevocadoRepository);
    }

    @Test
    void registrar_deberiaRechazarEmailDuplicado() {
        // Dado: el email del alta ya esta registrado.
        when(usuarioRepository.existsByEmail("nuevo@demo.com")).thenReturn(true);

        // Cuando / Entonces
        assertThrows(DuplicateResourceException.class, () -> service.register(registro("TRABAJADOR")));
        verify(trabajadorRepository, never()).save(any(Trabajador.class));
        verify(empleadorRepository, never()).save(any(Empleador.class));
    }

    @Test
    void registrar_deberiaRechazarRolInvalido() {
        // Dado
        when(usuarioRepository.existsByEmail("nuevo@demo.com")).thenReturn(false);

        // Cuando / Entonces
        assertThrows(BusinessRuleException.class, () -> service.register(registro("ADMINISTRADOR")));
        verify(trabajadorRepository, never()).save(any(Trabajador.class));
    }

    @Test
    void registrar_deberiaCrearTrabajadorConContrasenaCodificada() {
        // Dado
        when(usuarioRepository.existsByEmail("nuevo@demo.com")).thenReturn(false);
        when(passwordEncoder.encode("Clave123!")).thenReturn("hash-codificado");
        when(trabajadorRepository.save(any(Trabajador.class)))
                .thenAnswer(invocation -> {
                    Trabajador t = invocation.getArgument(0);
                    t.setId(20L);
                    return t;
                });
        when(jwtUtils.generateToken(any(Trabajador.class))).thenReturn("jwt-generado");
        prepararTokenDeVerificacion();

        // Cuando
        AuthResponseDTO respuesta = service.register(registro("TRABAJADOR"));

        // Entonces
        assertEquals("nuevo@demo.com", respuesta.getEmail());
        assertEquals("jwt-generado", respuesta.getToken());
        assertTrue(respuesta.getRoles().contains("ROLE_TRABAJADOR"));
        verify(passwordEncoder, times(1)).encode("Clave123!");
        verify(trabajadorRepository, times(1)).save(any(Trabajador.class));
    }

    @Test
    void registrar_deberiaCrearEmpleadorCuandoElRolEsEmpleador() {
        // Dado
        when(usuarioRepository.existsByEmail("nuevo@demo.com")).thenReturn(false);
        when(passwordEncoder.encode("Clave123!")).thenReturn("hash-codificado");
        when(empleadorRepository.save(any(Empleador.class)))
                .thenAnswer(invocation -> {
                    Empleador e = invocation.getArgument(0);
                    e.setId(21L);
                    return e;
                });
        when(jwtUtils.generateToken(any(Empleador.class))).thenReturn("jwt-generado");
        prepararTokenDeVerificacion();

        // Cuando
        AuthResponseDTO respuesta = service.register(registro("EMPLEADOR"));

        // Entonces
        assertTrue(respuesta.getRoles().contains("ROLE_EMPLEADOR"));
        verify(empleadorRepository, times(1)).save(any(Empleador.class));
        verify(trabajadorRepository, never()).save(any(Trabajador.class));
    }

    @Test
    void registrar_deberiaUsarTrabajadorCuandoNoSeIndicaRol() {
        // Dado
        when(usuarioRepository.existsByEmail("nuevo@demo.com")).thenReturn(false);
        when(passwordEncoder.encode("Clave123!")).thenReturn("hash-codificado");
        when(trabajadorRepository.save(any(Trabajador.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtils.generateToken(any(Trabajador.class))).thenReturn("jwt-generado");
        prepararTokenDeVerificacion();

        // Cuando
        AuthResponseDTO respuesta = service.register(registro(null));

        // Entonces
        assertTrue(respuesta.getRoles().contains("ROLE_TRABAJADOR"));
    }

    @Test
    void registrar_deberiaDevolverLaIdentidadMinimaYElTokenDeVerificacion() {
        // AL-API-005: la respuesta del registro lleva el id del usuario, para que el
        // cliente no tenga que listar usuarios para descubrir el suyo.
        // AL-SEC-004: además emite un token de verificación de correo de un solo uso.
        when(usuarioRepository.existsByEmail("nuevo@demo.com")).thenReturn(false);
        when(passwordEncoder.encode("Clave123!")).thenReturn("hash-codificado");
        when(trabajadorRepository.save(any(Trabajador.class)))
                .thenAnswer(invocation -> {
                    Trabajador t = invocation.getArgument(0);
                    t.setId(20L);
                    return t;
                });
        when(jwtUtils.generateToken(any(Trabajador.class))).thenReturn("jwt-generado");
        prepararTokenDeVerificacion();

        // Cuando
        AuthResponseDTO respuesta = service.register(registro("TRABAJADOR"));

        // Entonces
        assertEquals(20L, respuesta.getId());
        assertEquals("Nuevo Usuario", respuesta.getNombre());
        assertEquals("token-verificacion", respuesta.getVerificacionEmailToken());
    }

    @Test
    void login_deberiaDevolverTokenYRoles() {
        // Dado
        Trabajador trabajador = new Trabajador();
        trabajador.setId(3L);
        trabajador.setNombre("Demo Trabajador");
        trabajador.setEmail("trabajador@demo.com");
        trabajador.setPassword("hash");
        Authentication autenticacion = new UsernamePasswordAuthenticationToken(
                trabajador, null, trabajador.getAuthorities());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(autenticacion);
        when(jwtUtils.generateToken(trabajador)).thenReturn("jwt-generado");

        // Cuando
        AuthResponseDTO respuesta = service.login(credenciales("trabajador@demo.com", "Clave123!"));

        // Entonces
        assertEquals("jwt-generado", respuesta.getToken());
        assertEquals("trabajador@demo.com", respuesta.getEmail());
        assertTrue(respuesta.getRoles().contains("ROLE_TRABAJADOR"));
    }

    @Test
    void login_deberiaPropagarElErrorDeCredencialesInvalidas() {
        // Dado
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Cuando / Entonces
        assertThrows(BadCredentialsException.class,
                () -> service.login(credenciales("trabajador@demo.com", "incorrecta")));
        verify(jwtUtils, never()).generateToken(any());
    }

    @Test
    void login_noDebeDevolverElPasswordEnLaRespuesta() {
        // Dado
        Trabajador trabajador = new Trabajador();
        trabajador.setId(3L);
        trabajador.setNombre("Demo Trabajador");
        trabajador.setEmail("trabajador@demo.com");
        trabajador.setPassword("hash-secreto");
        Authentication autenticacion = new UsernamePasswordAuthenticationToken(
                trabajador, null, trabajador.getAuthorities());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(autenticacion);
        when(jwtUtils.generateToken(trabajador)).thenReturn("jwt-generado");

        // Cuando
        AuthResponseDTO respuesta = service.login(credenciales("trabajador@demo.com", "Clave123!"));

        // Entonces: la respuesta solo lleva token, email y roles; jamas la contrasena.
        boolean exponePassword = java.util.Arrays.stream(AuthResponseDTO.class.getDeclaredFields())
                .anyMatch(campo -> campo.getName().toLowerCase().contains("password"));
        assertTrue(!exponePassword, "AuthResponseDTO no debe exponer el password");
        assertEquals("trabajador@demo.com", respuesta.getEmail());
    }

    /**
     * Prepara el token de verificación de correo que emite el registro (AL-SEC-004).
     *
     * <p>El servicio guarda el {@code jti} del token y su fecha de expiración.</p>
     */
    private void prepararTokenDeVerificacion() {
        when(jwtUtils.generateVerificationToken("nuevo@demo.com"))
                .thenReturn(new JwtUtils.ScopedToken("token-verificacion", "jti-verificacion"));
        when(jwtUtils.extractExpiration("token-verificacion"))
                .thenReturn(new Date(System.currentTimeMillis() + 86_400_000L));
    }

    private RegisterRequestDTO registro(String rol) {        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setNombre("Nuevo Usuario");
        dto.setEmail("nuevo@demo.com");
        dto.setPassword("Clave123!");
        dto.setTelefono("+549110000999");
        dto.setRol(rol);
        return dto;
    }

    private AuthRequestDTO credenciales(String email, String password) {
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }
}
