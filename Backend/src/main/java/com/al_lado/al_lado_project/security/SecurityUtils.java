package com.al_lado.al_lado_project.security;

import com.al_lado.al_lado_project.entity.usuario.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Comprueba si el usuario puede operar sobre un recurso.
 *
 * <p>La sesión usa una entidad {@link Usuario} como principal. Si no hay sesión válida o el
 * principal tiene otro tipo, {@link #currentUserId()} devuelve null. Los permisos de rol se
 * comprueban además en los endpoints.</p>
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /** @return la autenticación del contexto de seguridad o {@code null} si no hay sesión. */
    public static Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * @return ID del usuario autenticado, o {@code null} si no hay sesión válida o
     *         el principal no es una entidad {@link Usuario}.
     */
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

    /**
     * @return {@code true} si la sesión actual tiene autoridad {@code ROLE_ADMIN}.
     *         Sin autenticación devuelve {@code false} (no lanza).
     */
    public static boolean isAdmin() {
        Authentication authentication = currentAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    /**
     * Permite operar al titular del recurso o a ADMIN.
     *
     * <p>Un ID de propietario nulo se rechaza incluso para ADMIN.</p>
     *
     * @param userId ID del propietario del recurso sobre el que se opera
     * @throws org.springframework.security.access.AccessDeniedException
     *     si el propietario es nulo o quien ejecuta no es el titular ni ADMIN
     */
    public static void requireSelfOrAdmin(Long userId) {
        if (userId == null || (!isAdmin() && !userId.equals(currentUserId()))) {
            throw new AccessDeniedException("No tiene permisos para operar sobre recursos de otro usuario.");
        }
    }
}
