package com.al_lado.e2e.tests.empleador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.pages.EmpleadorProfilePage;

/**
 * Casos de visualización de perfil del empleador (area HU-05).
 *
 * <p>Verifica que un trabajador puede ver el perfil del empleador que publicó
 * una necesidad: historial de oportunidades publicadas y perfil empresarial.</p>
 */
@DisplayName("HU-05 - Ver perfil del empleador")
class PerfilEmpleadorTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-05 CA-01: el trabajador ve el perfil del empleador")
    void trabajadorVePerfilEmpleador() {
        loginAsWorker();

        EmpleadorProfilePage perfil = new EmpleadorProfilePage(driver).open();

        assertThat(perfil.nombre()).isNotBlank();
    }

    @Test
    @DisplayName("HU-05: sin sesión no se puede ver el perfil del empleador")
    void sinSesionNoPuedeVerPerfilEmpleador() {
        driver.get(E2EConfig.baseUrl() + "/empleador/perfil");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
