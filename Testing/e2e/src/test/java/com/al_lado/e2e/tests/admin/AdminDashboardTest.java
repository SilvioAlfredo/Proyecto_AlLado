package com.al_lado.e2e.tests.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.AdminDashboardPage;

/**
 * Casos de panel de mÃ©tricas de administraciÃ³n (area HU-19).
 *
 * <p>Verifica que un administrador autenticado con {@code ROLE_ADMIN} puede
 * consultar el panel con mÃ©tricas de la plataforma: contratos, trabajadores,
 * promedio de remuneraciÃ³n, usuarios activos/inactivos, postulaciones por
 * estado y tendencias.</p>
 */
@DisplayName("HU-19 - Panel de mÃ©tricas de administraciÃ³n")
class AdminDashboardTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-19 CA-01: el administrador consulta el panel de mÃ©tricas")
    void administradorConsultaPanelDeMetricas() {
        loginAsAdmin();

        AdminDashboardPage page = new AdminDashboardPage(driver).open();

        // Todas las mÃ©tricas deberían estar presentes (pueden ser cero si no hay datos)
        assertThat(page.metricContracts()).isNotBlank();
        assertThat(page.metricWorkers()).isNotBlank();
        assertThat(page.metricIncome()).isNotBlank();
        assertThat(page.metricCompleted()).isNotBlank();
    }

    @Test
    @DisplayName("HU-19: el panel de mÃ©tricas no estÃ¡ disponible sin sesiÃ³n")
    void panelDeMetricasNoDisponibleSinSesion() {
        driver.get(E2EConfig.baseUrl() + "/admin/dashboard");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/acceso-admin"));

        assertThat(driver.getCurrentUrl()).contains("/acceso-admin");
    }

    @Test
    @DisplayName("HU-19: un trabajador no puede acceder al panel de mÃ©tricas")
    void trabajadorNoPuedeAccederAPanelDeMetricas() {
        loginAsWorker();

        driver.get(E2EConfig.baseUrl() + "/admin/dashboard");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> !d.getCurrentUrl().contains("/admin/dashboard"));

        assertThat(driver.getCurrentUrl()).doesNotContain("/admin/dashboard");
    }

    @Test
    @DisplayName("HU-19: un empleador no puede acceder al panel de mÃ©tricas")
    void empleadorNoPuedeAccederAPanelDeMetricas() {
        loginAsEmployer();

        driver.get(E2EConfig.baseUrl() + "/admin/dashboard");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> !d.getCurrentUrl().contains("/admin/dashboard"));

        assertThat(driver.getCurrentUrl()).doesNotContain("/admin/dashboard");
    }
}
