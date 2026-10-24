package com.al_lado.e2e.tests.trabajador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.core.Waits;
import com.al_lado.e2e.pages.LoginPage;

/**
 * Casos de proteccion de rutas del area de trabajador.
 *
 * <p>El {@code AuthGuard} no muestra un mensaje de denegacion: renderiza
 * {@code null} y redirige. Por eso el oraculo es la URL final, no un mensaje.</p>
 */
@DisplayName("TC-SEC - Proteccion de rutas del trabajador")
class ProtectedRouteTest extends BaseE2ETest {

    @Test
    @DisplayName("TC-SEC-001: sin sesion, el panel del trabajador redirige al ingreso")
    void sinSesionElPanelRedirigeAlIngreso() {
        driver.get(E2EConfig.baseUrl() + "/trabajador/dashboard");

        Waits.urlContains(driver, "/login");
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("TC-SEC-001: una cuenta de empleador no entra al area de trabajador")
    void empleadorNoEntraAlAreaDeTrabajador() {
        loginAsEmployer();

        driver.get(E2EConfig.baseUrl() + "/trabajador/dashboard");

        // El guard de rol redirige a la home: no hay mensaje de denegacion.
        Waits.waitUntil(driver, java.time.Duration.ofSeconds(10),
                d -> !d.getCurrentUrl().contains("/trabajador/dashboard"));
        assertThat(driver.getCurrentUrl()).doesNotContain("/trabajador/dashboard");
    }
}
