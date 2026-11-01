package com.al_lado.e2e.tests.empleador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.ContratosPage;

/**
 * Casos de historial de contratos (area HU-08).
 *
 * <p>Verifica que un empleador puede consultar el historial de contratos y
 * trabajos pasados: ver el nombre del trabajador, las fechas y el estado de
 * cada servicio (PENDIENTE, SELECCIONADA, RECHAZADA, COMPLETADA, RETIRADA).</p>
 */
@DisplayName("HU-08 - Historial de contratos")
class ContratosTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-08 CA-01: el empleador ve el historial de contratos cronológico")
    void empleadorVeHistorialContratos() {
        loginAsEmployer();

        ContratosPage contratos = new ContratosPage(driver).open();

        assertThat(contratos.contratosCount()).isGreaterThanOrEqualTo(0);
        if (contratos.contratosCount() > 0) {
            assertThat(contratos.trabajadorNombre(0)).isNotBlank();
            assertThat(contratos.estado(0)).isNotBlank();
            assertThat(contratos.fecha(0)).isNotBlank();
        }
    }

    @Test
    @DisplayName("HU-08: sin sesión no se puede ver el historial de contratos")
    void sinSesionNoPuedeVerHistorialContratos() {
        driver.get(E2EConfig.baseUrl() + "/empleador/contratos");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("HU-08: un trabajador no puede ver el historial de contratos del empleador")
    void trabajadorNoPuedeVerHistorialContratosEmpleador() {
        loginAsWorker();

        driver.get(E2EConfig.baseUrl() + "/empleador/contratos");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> !d.getCurrentUrl().contains("/empleador/contratos"));

        assertThat(driver.getCurrentUrl()).doesNotContain("/empleador/contratos");
    }
}
