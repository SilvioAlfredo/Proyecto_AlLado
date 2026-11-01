package com.al_lado.e2e.tests.trabajador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.SuscripcionPage;

/**
 * Casos de adquirir nivel PRO por transferencia (area HU-16).
 *
 * <p>Verifica que un trabajador puede adquirir el nivel PRO realizando un
 * aporte por transferencia.</p>
 */
@DisplayName("HU-16 - Adquirir nivel PRO por transferencia")
class SuscripcionTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-16 CA-01: el trabajador ve su plan de suscripción actual")
    void trabajadorVePlanSuscripcion() {
        loginAsWorker();

        SuscripcionPage suscripcion = new SuscripcionPage(driver).openTrabajador();

        assertThat(suscripcion.planActual()).isNotBlank();
        assertThat(suscripcion.estado()).isNotBlank();
    }

    @Test
    @DisplayName("HU-16: sin sesión no se puede ver la suscripción")
    void sinSesionNoPuedeVerSuscripcion() {
        driver.get(E2EConfig.baseUrl() + "/trabajador/perfil/suscripcion");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
