package com.al_lado.e2e.tests.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.AdminReportsPage;

/**
 * Casos de moderaciÃ³n de reportes (area HU-18).
 *
 * <p>Verifica que un administrador puede moderar reportes en estado
 * {@code PENDIENTE} indicando {@code EN_REVISION}, {@code RESUELTO} o
 * {@code DESCARTADO}, y que un reporte ya moderado no puede volver a
 * resolverse.</p>
 */
@DisplayName("HU-18 - ModeraciÃ³n de reportes")
class AdminReportsTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-18 CA-01: el administrador modera un reporte pendiente")
    void administradorModeraReportePendiente() {
        loginAsAdmin();

        AdminReportsPage page = new AdminReportsPage(driver).open();
        assertThat(page.reportCount()).isGreaterThan(0);

        page.openResolveDialog(0);
        page.selectResolveStatus("RESUELTO");
        page.confirmResolve();

        assertThat(page.isSuccess()).isTrue();
        assertThat(page.successMessage()).isNotBlank();
    }

    @Test
    @DisplayName("HU-18 CA-01: un reporte ya moderado no puede volver a resolverse")
    void reporteYaModeradoNoSePuedeResolverNuevamente() {
        loginAsAdmin();

        AdminReportsPage page = new AdminReportsPage(driver).open();
        assertThat(page.reportCount()).isGreaterThan(0);

        // Primera resoluciÃ³n
        page.openResolveDialog(0);
        page.selectResolveStatus("RESUELTO");
        page.confirmResolve();
        assertThat(page.isSuccess()).isTrue();

        // Segundo intento sobre el mismo reporte: deberÃ­a rechazarse
        page.openResolveDialog(0);
        page.selectResolveStatus("DESCARTADO");
        page.confirmResolve();

        assertThat(page.isError()).isTrue();
        assertThat(page.errorMessage()).isNotBlank();
    }

    @Test
    @DisplayName("HU-18: el administrador no puede moderar sin sesiÃ³n")
    void sinSesionNoPuedeModerar() {
        driver.get(E2EConfig.baseUrl() + "/admin/moderaciÃ³n");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/acceso-admin"));

        assertThat(driver.getCurrentUrl()).contains("/acceso-admin");
    }

    @Test
    @DisplayName("HU-18: un trabajador no puede acceder a la moderaciÃ³n de reportes")
    void trabajadorNoPuedeAccederAModeracion() {
        loginAsWorker();

        driver.get(E2EConfig.baseUrl() + "/admin/moderaciÃ³n");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> !d.getCurrentUrl().contains("/admin/moderaciÃ³n"));

        assertThat(driver.getCurrentUrl()).doesNotContain("/admin/moderaciÃ³n");
    }
}
