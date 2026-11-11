package com.al_lado.e2e.tests.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.core.Waits;
import com.al_lado.e2e.pages.LoginPage;

/**
 * Casos del acceso restringido de administracion (area TC-SEC del plan).
 *
 * <p>El panel de administracion usa un guard propio ({@code AdminAccess}) distinto
 * del {@code AuthGuard} del area de trabajador: sin sesion redirige a
 * {@code /acceso-admin} y con un rol no autorizado devuelve al panel propio.</p>
 */
@DisplayName("TC-SEC-001 - Autorizacion del panel de administracion")
class AdminAccessTest extends BaseE2ETest {

    @Test
    @DisplayName("TC-SEC-001: el administrador autenticado entra al panel")
    void administradorEntraAlPanel() {
        LoginPage login = new LoginPage(driver).openAdminAccess();

        login.login(E2EConfig.adminEmail(), E2EConfig.adminPassword());

        assertThat(driver.getCurrentUrl()).contains("/admin/dashboard");
        assertThat(login.storedRoles()).contains("ROLE_ADMIN");
    }

    @Test
    @DisplayName("TC-SEC-001: una cuenta sin rol ADMIN no entra al panel")
    void trabajadorSinPermisoDeAdminEsRechazado() {
        LoginPage login = new LoginPage(driver).openAdminAccess();

        login.login(E2EConfig.workerEmail(), E2EConfig.workerPassword());

        assertThat(login.hasError()).isTrue();
        assertThat(login.errorMessage()).containsIgnoringCase("administración");
        assertThat(driver.getCurrentUrl()).doesNotContain("/admin/dashboard");
        assertThat(login.hasStoredSession()).isFalse();
    }

    @Test
    @DisplayName("TC-SEC-001: sin sesion, el panel de administracion redirige al acceso restringido")
    void sinSesionElPanelRedirigeAlAccesoRestringido() {
        driver.get(E2EConfig.baseUrl() + "/admin/dashboard");

        // El guard de administracion redirige tras la hidratacion de la pagina.
        Waits.urlContains(driver, "/acceso-admin");
        assertThat(driver.getCurrentUrl()).contains("/acceso-admin");
    }
}
