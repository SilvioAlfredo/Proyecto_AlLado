package com.al_lado.al_lado_project.service.auth.impl;

import com.al_lado.al_lado_project.dto.auth.AuthRequestDTO;
import com.al_lado.al_lado_project.dto.auth.AuthResponseDTO;
import com.al_lado.al_lado_project.dto.auth.ChangePasswordDTO;
import com.al_lado.al_lado_project.dto.auth.ForgotPasswordRequestDTO;
import com.al_lado.al_lado_project.dto.auth.IdentidadDTO;
import com.al_lado.al_lado_project.dto.auth.RegisterRequestDTO;
import com.al_lado.al_lado_project.dto.auth.ResetPasswordRequestDTO;
import com.al_lado.al_lado_project.entity.seguridad.TokenRevocado;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
import com.al_lado.al_lado_project.security.jwt.TokenPurpose;
import com.al_lado.al_lado_project.service.auth.AuthService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /**
     * Respuesta del flujo de recuperación (AL-SEC-005).
     *
     * <p>Es la misma exista o no la cuenta: no permite descubrir qué emails están
     * registrados.</p>
     */
    public static final String MENSAJE_RECUPERACION_GENERICO = AuthService.MENSAJE_RECUPERACION_GENERICO;

    /** Mensaje único para los tokens rechazados: no filtra el motivo real (AL-SEC-004). */
    public static final String MENSAJE_TOKEN_INVALIDO = AuthService.MENSAJE_TOKEN_INVALIDO;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final EmpleadorRepository empleadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRevocadoRepository tokenRevocadoRepository;

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);

        return AuthResponseDTO.builder()
                .token(jwt)
                .id(identificadorDe(userDetails))
                .email(userDetails.getUsername())
                .nombre(nombreDe(userDetails))
                .roles(rolesDe(userDetails))
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("El email ya se encuentra registrado.");
        }

        String rol = request.getRol() != null ? request.getRol().toUpperCase() : "TRABAJADOR";
        if (!"EMPLEADOR".equals(rol) && !"TRABAJADOR".equals(rol)) {
            throw new BusinessRuleException("Rol inválido. Valores permitidos: TRABAJADOR, EMPLEADOR.");
        }
        Usuario usuario;

        if ("EMPLEADOR".equals(rol)) {
            Empleador empleador = new Empleador();
            empleador.setNombre(request.getNombre());
            empleador.setEmail(request.getEmail());
            empleador.setPassword(passwordEncoder.encode(request.getPassword()));
            empleador.setTelefono(request.getTelefono());
            usuario = empleadorRepository.save(empleador);
        } else {
            Trabajador trabajador = new Trabajador();
            trabajador.setNombre(request.getNombre());
            trabajador.setEmail(request.getEmail());
            trabajador.setPassword(passwordEncoder.encode(request.getPassword()));
            trabajador.setTelefono(request.getTelefono());
            usuario = trabajadorRepository.save(trabajador);
        }

        String jwt = jwtUtils.generateToken(usuario);

        // Token de verificación de correo: propósito propio, expira y se consume una vez.
        // El token anterior queda invalidado al emitir uno nuevo.
        JwtUtils.ScopedToken verificacion = jwtUtils.generateVerificationToken(usuario.getEmail());
        usuario.setEmailVerificationToken(verificacion.jti());
        usuario.setEmailVerificationTokenExpiry(expiracionDe(verificacion.token()));
        usuarioRepository.save(usuario);

        return AuthResponseDTO.builder()
                .token(jwt)
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .roles(rolesDe(usuario))
                .verificacionEmailToken(verificacion.token())
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        TokenConsumible consumible = tokenConsumible(token, TokenPurpose.VERIFY_EMAIL);

        Usuario usuario = usuarioRepository.findByEmail(consumible.email())
                .orElseThrow(() -> new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO));

        // Uso único: el token registrado debe ser exactamente el recibido.
        if (!consumible.jti().equals(usuario.getEmailVerificationToken())) {
            throw new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO);
        }
        if (usuario.getEmailVerificationTokenExpiry() == null
                || usuario.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO);
        }

        usuario.setEmailVerificado(true);
        usuario.clearEmailVerificationToken();
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public String solicitarVerificacionEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        JwtUtils.ScopedToken verificacion = jwtUtils.generateVerificationToken(usuario.getEmail());
        usuario.setEmailVerificationToken(verificacion.jti());
        usuario.setEmailVerificationTokenExpiry(expiracionDe(verificacion.token()));
        usuarioRepository.save(usuario);
        return verificacion.token();
    }

    @Override
    @Transactional(readOnly = true)
    public IdentidadDTO obtenerIdentidad(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
        return IdentidadDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .roles(rolesDe(usuario))
                .build();
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }

        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        // Un cambio de contraseña invalida cualquier recuperación pendiente.
        usuario.clearPasswordResetToken();
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyCurrentPassword(ChangePasswordDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);

        // AL-SEC-005: el mismo trabajo en los dos caminos. La codificación BCrypt (la
        // operación más cara del flujo) se ejecuta exista o no la cuenta, de modo que el
        // tiempo de respuesta no delate si el email está registrado. La consulta al
        // repositorio también corre en ambos casos.
        passwordEncoder.encode("al-lado-trabajo-descartable");

        if (usuario == null) {
            // No se revela la existencia de la cuenta: la respuesta pública es idéntica.
            return;
        }

        // Token de recuperación: propósito propio, expira y se consume una vez.
        JwtUtils.ScopedToken recuperacion = jwtUtils.generateRecoveryToken(usuario.getEmail());
        usuario.setPasswordResetToken(recuperacion.jti());
        usuario.setPasswordResetTokenExpiry(expiracionDe(recuperacion.token()));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        TokenConsumible consumible = tokenConsumible(request.getToken(), TokenPurpose.RECOVERY);

        Usuario usuario = usuarioRepository.findByEmail(consumible.email())
                .orElseThrow(() -> new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO));

        // Uso único: el token registrado debe ser exactamente el recibido.
        if (!consumible.jti().equals(usuario.getPasswordResetToken())) {
            throw new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO);
        }
        if (usuario.getPasswordResetTokenExpiry() == null
                || usuario.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO);
        }

        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        // Se elimina el token: no puede reutilizarse.
        usuario.clearPasswordResetToken();
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void logout(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token ausente para cerrar sesión.");
        }
        String token = bearerToken.substring(7);
        final String jti;
        final String sujeto;
        final Date expiracion;
        try {
            jti = jwtUtils.extractTokenId(token);
            sujeto = jwtUtils.extractUsername(token);
            expiracion = jwtUtils.extractExpiration(token);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Token inválido o expirado.");
        }
        if (jti == null) {
            throw new IllegalArgumentException("Token sin identificador: no revocable, espere su expiración.");
        }
        // Purga entradas vencidas y registra la revocación (se guarda el jti,
        // nunca el token completo).
        tokenRevocadoRepository.deleteByExpiraBefore(LocalDateTime.now());
        if (!tokenRevocadoRepository.existsByJti(jti)) {
            tokenRevocadoRepository.save(new TokenRevocado(jti, sujeto,
                    LocalDateTime.ofInstant(expiracion.toInstant(), ZoneId.systemDefault())));
        }
    }

    // ==================================================================
    // Helpers
    // ==================================================================

    /**
     * Valida un token de un solo uso: firma, expiración, propósito e identificador.
     *
     * <p>Un token de sesión, uno de recuperación usado para verificar un email, o un token
     * sin {@code jti} se rechazan con el mismo mensaje genérico (AL-SEC-004).</p>
     *
     * @param token   token recibido
     * @param purpose flujo para el que el token debe haber sido emitido
     * @return email y {@code jti} del token
     * @throws IllegalArgumentException si el token no es válido para ese flujo
     */
    private TokenConsumible tokenConsumible(String token, TokenPurpose purpose) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO);
        }
        final String email;
        final String jti;
        final TokenPurpose proposito;
        try {
            email = jwtUtils.extractUsername(token);
            jti = jwtUtils.extractTokenId(token);
            proposito = jwtUtils.extractPurpose(token);
        } catch (JwtException | IllegalArgumentException e) {
            // Token malformado, con firma inválida o expirado: mensaje genérico.
            throw new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO);
        }
        if (email == null || email.isBlank() || jti == null || jti.isBlank() || proposito != purpose) {
            throw new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO);
        }
        return new TokenConsumible(email, jti);
    }

    /** Fecha de expiración declarada por el token, en la zona horaria del servidor. */
    private LocalDateTime expiracionDe(String token) {
        Date expiracion = jwtUtils.extractExpiration(token);
        if (expiracion == null) {
            throw new IllegalStateException("El token emitido no declara su expiración.");
        }
        return LocalDateTime.ofInstant(expiracion.toInstant(), ZoneId.systemDefault());
    }

    private Long identificadorDe(UserDetails userDetails) {
        if (userDetails instanceof Usuario usuario) {
            return usuario.getId();
        }
        return null;
    }

    private String nombreDe(UserDetails userDetails) {
        if (userDetails instanceof Usuario usuario) {
            return usuario.getNombre();
        }
        return null;
    }

    private List<String> rolesDe(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * Datos mínimos de un token de un solo uso.
     *
     * @param email email del titular del token
     * @param jti   identificador único del token (registrado en la base)
     */
    private record TokenConsumible(String email, String jti) {
    }
}
