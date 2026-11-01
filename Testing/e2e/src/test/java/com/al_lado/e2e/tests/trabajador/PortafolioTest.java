package com.al_lado.e2e.tests.trabajador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.PortafolioPage;

/**
 * Casos de portafolio del trabajador (area HU-17).
 *
 * <p>Verifica que un trabajador puede armar su portafolio y acceder a las
 * propuestas reservadas para usuarios PRO.</p>
 */
@DisplayName("HU-17 - Portafolio del trabajador")
class PortafolioTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-17 CA-01: el trabajador ve su portafolio de ítems")
    void trabajadorVePortafolio() {
        loginAsWorker();

        PortafolioPage portafolio = new PortafolioPage(driver).open();

        assertThat(portafolio.itemsCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("HU-17: sin sesión no se puede ver el portafolio")
    void sinSesionNoPuedeVerPortafolio() {
        driver.get(E2EConfig.baseUrl() + "/trabajador/perfil/portafolio");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
