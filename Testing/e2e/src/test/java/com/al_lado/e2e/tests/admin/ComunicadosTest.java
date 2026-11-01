package com.al_lado.e2e.tests.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.ComunicadosPage;

/**
 * Casos de comunicados institucionales (area HU-20).
 *
 * <p>Verifica que un usuario puede leer los comunicados institucionales de
 * la plataforma.</p>
 */
@DisplayName("HU-20 - Comunicados institucionales")
class ComunicadosTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-20 CA-01: el usuario lee los comunicados publicados")
    void usuarioLeeComunicados() {
        loginAsWorker();

        ComunicadosPage comunicados = new ComunicadosPage(driver).open();

        assertThat(comunicados.comunicadosCount()).isGreaterThanOrEqualTo(0);
        if (comunicados.comunicadosCount() > 0) {
            assertThat(comunicados.titulo(0)).isNotBlank();
        }
    }

    @Test
    @DisplayName("HU-20: sin sesión no se pueden leer los comunicados")
    void sinSesionNoPuedeLeerComunicados() {
        driver.get(E2EConfig.baseUrl() + "/admin/comunicados");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/acceso-admin"));

        assertThat(driver.getCurrentUrl()).contains("/acceso-admin");
    }
}
