package com.al_lado.al_lado_project.security;

import com.al_lado.al_lado_project.entity.usuario.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilidades para verificaciones de titularidad en la capa de servicio.
 * El principal autenticado es la propia entidad {@link Usuario} (ver
 * UserDetailsServiceImpl), por lo que se puede extraer su id de forma segura.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /** Id del usuario autenticado o {@code null} si no hay sesión válida. */
    public static Long currentUserId() {
        Authentication authentication = currentAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Usuario usuario) {
            return usuario.getId();
        }
        return null;
    }

    public static boolean isAdmin() {
        Authentication authentication = currentAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    /** Permite operar solo al titular del recurso o a un administrador. */
    public static void requireSelfOrAdmin(Long userId) {
        if (userId == null || (!isAdmin() && !userId.equals(currentUserId()))) {
            throw new AccessDeniedException("No tiene permisos para operar sobre recursos de otro usuario.");
        }
    }
}
