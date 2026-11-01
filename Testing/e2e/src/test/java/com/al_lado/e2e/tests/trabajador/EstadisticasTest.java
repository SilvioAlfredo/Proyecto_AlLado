package com.al_lado.e2e.tests.trabajador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.EstadisticasPage;

/**
 * Casos de estadísticas de ingresos (area HU-12).
 *
 * <p>Verifica que un trabajador puede ver estadísticas de ingresos estimados
 * y trabajos realizados en su panel de control.</p>
 */
@DisplayName("HU-12 - Estadísticas de ingresos")
class EstadisticasTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-12 CA-01: el trabajador ve sus estadísticas de ingresos")
    void trabajadorVeEstadisticasIngresos() {
        loginAsWorker();

        EstadisticasPage estadisticas = new EstadisticasPage(driver).open();

        assertThat(estadisticas.ingresosTotales()).isNotBlank();
        assertThat(estadisticas.postulacionesEnviadas()).isNotBlank();
        assertThat(estadisticas.postulacionesCompletadas()).isNotBlank();
        assertThat(estadisticas.valoracionesPromedio()).isNotBlank();
    }

    @Test
    @DisplayName("HU-12: sin sesión no se pueden ver las estadísticas")
    void sinSesionNoPuedeVerEstadisticas() {
        driver.get(E2EConfig.baseUrl() + "/trabajador/perfil/estadisticas");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
