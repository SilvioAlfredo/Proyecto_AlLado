package com.al_lado.al_lado_project.service.auth.impl;

import com.al_lado.al_lado_project.dto.auth.AuthRequestDTO;
import com.al_lado.al_lado_project.dto.auth.AuthResponseDTO;
import com.al_lado.al_lado_project.dto.auth.ChangePasswordDTO;
import com.al_lado.al_lado_project.dto.auth.ForgotPasswordRequestDTO;
import com.al_lado.al_lado_project.dto.auth.RegisterRequestDTO;
import com.al_lado.al_lado_project.dto.auth.ResetPasswordRequestDTO;
import com.al_lado.al_lado_project.entity.seguridad.TokenRevocado;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.security.jwt.JwtUtils;
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

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return AuthResponseDTO.builder()
                .token(jwt)
                .email(userDetails.getUsername())
                .roles(roles)
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
        List<String> roles = usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return AuthResponseDTO.builder()
                .token(jwt)
                .email(usuario.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        final String email;
        try {
            email = jwtUtils.extractUsername(token);
        } catch (JwtException | IllegalArgumentException e) {
            // Token malformado, con firma invalida o expirado: mensaje
            // generico al cliente, sin filtrar detalles internos del parser.
            throw new IllegalArgumentException("Token de verificación inválido o expirado.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Token de verificación inválido o expirado.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        // Marcar email como verificado
        usuario.setEmailVerificado(true);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getCurrentPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }

        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyCurrentPassword(ChangePasswordDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getCurrentPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + request.getEmail()));
        
        // Generar token de recuperación
        String token = java.util.UUID.randomUUID().toString();
        usuario.setPasswordResetToken(token);
        usuario.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(24)); // Token válido 24 horas
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        Usuario usuario = usuarioRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token de restablecimiento inválido o expirado."));
        
        // Verificar que el token no haya expirado
        if (usuario.getPasswordResetTokenExpiry() == null || usuario.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token de restablecimiento expirado. Solicite uno nuevo.");
        }
        
        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        
        // Limpiar campos de token
        usuario.setPasswordResetToken(null);
        usuario.setPasswordResetTokenExpiry(null);
        
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
}