package com.al_lado.al_lado_project.security;

import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** AL-TEST-001: reglas titular-o-admin de SecurityUtils sin contexto Spring. */
class SecurityUtilsTest {

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private static void autenticarComo(Usuario usuario) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    private static Trabajador trabajador(Long id) {
        Trabajador t = new Trabajador();
        t.setId(id);
        t.setNombre("Ana");
        t.setEmail("ana@mail.com");
        t.setPassword("x");
        return t;
    }

    private static Administrador admin(Long id) {
        Administrador a = new Administrador();
        a.setId(id);
        a.setNombre("Root");
        a.setEmail("root@mail.com");
        a.setPassword("x");
        return a;
    }

    @Test
    void titularPuedeOperarSusRecursos() {
        autenticarComo(trabajador(7L));
        assertDoesNotThrow(() -> SecurityUtils.requireSelfOrAdmin(7L));
    }

    @Test
    void terceroNoPuedeOperarRecursosAjenos() {
        autenticarComo(trabajador(7L));
        assertThrows(AccessDeniedException.class, () -> SecurityUtils.requireSelfOrAdmin(9L));
    }

    @Test
    void adminPuedeOperarRecursosAjenos() {
        autenticarComo(admin(1L));
        assertDoesNotThrow(() -> SecurityUtils.requireSelfOrAdmin(9L));
    }

    @Test
    void anonimoNoPuedeOperar() {
        SecurityContextHolder.clearContext();
        assertThrows(AccessDeniedException.class, () -> SecurityUtils.requireSelfOrAdmin(7L));
    }

    @Test
    void idNuloSiempreDenegado() {
        autenticarComo(admin(1L));
        assertThrows(AccessDeniedException.class, () -> SecurityUtils.requireSelfOrAdmin(null));
    }
}
