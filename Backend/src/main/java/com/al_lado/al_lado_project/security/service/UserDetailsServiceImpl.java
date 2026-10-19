package com.al_lado.al_lado_project.security.service;

import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Busca al usuario por email y lo utiliza como principal de seguridad.
 *
 * <p>Los roles dependen del tipo de usuario en la base. Rechaza cuentas inexistentes o
 * deshabilitadas; no exige email verificado.</p>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * @param email email del usuario (nombre de usuario en Spring Security)
     * @return la entidad usuario autenticable, con sus roles vigentes de la BD
     * @throws UsernameNotFoundException si no existe
     * @throws DisabledException         si la cuenta está deshabilitada
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails userDetails = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));
        if (!userDetails.isEnabled()) {
            throw new DisabledException("Usuario deshabilitado.");
        }
        return userDetails;
    }
}