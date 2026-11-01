package com.al_lado.e2e.tests.horarios;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.WorkerSchedulePage;

/**
 * Casos de configuraciÃ³n de dÃ­as y rangos horarios de servicio (area HU-10).
 *
 * <p>Verifica que un trabajador puede configurar sus dÃ­as y rangos horarios
 * de servicio, y que el sistema rechaza franjas que se solapan con otra ya
 * existente.</p>
 */
@DisplayName("HU-10 - Configurar dÃ­as y rangos horarios de servicio")
class ScheduleTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-10 CA-01: registro de franja horaria exitosa")
    void registroDeFranjaHorariaExitosa() {
        loginAsWorker();

        WorkerSchedulePage form = new WorkerSchedulePage(driver).open();
        form.fillDay("Lunes");
        form.fillStartTime("09:00");
        form.fillEndTime("12:00");
        form.submit();

        assertThat(form.isSuccess()).isTrue();
        assertThat(form.successMessage()).isNotBlank();
        assertThat(form.scheduleCount()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("HU-10 CA-02: control de solapamiento - franja que se solapa se rechaza")
    void franjaQueSeSolapaSeRechaza() {
        loginAsWorker();

        WorkerSchedulePage form = new WorkerSchedulePage(driver).open();
        // Primera franja: Lunes 09:00 - 12:00
        form.fillDay("Lunes");
        form.fillStartTime("09:00");
        form.fillEndTime("12:00");
        form.submit();

        // Segunda franja que se solapa: Lunes 10:00 - 11:00
        form.fillDay("Lunes");
        form.fillStartTime("10:00");
        form.fillEndTime("11:00");
        form.submit();

        assertThat(form.isError()).isTrue();
        assertThat(form.errorMessage()).isNotBlank();
        assertThat(form.errorMessage()).toLowerCase().contains("solap");
    }

    @Test
    @DisplayName("HU-10 CA-02: franja que no se solapa se acepta")
    void franjaQueNoSeSolapaSeAcepta() {
        loginAsWorker();

        WorkerSchedulePage form = new WorkerSchedulePage(driver).open();
        // Primera franja: Lunes 09:00 - 12:00
        form.fillDay("Lunes");
        form.fillStartTime("09:00");
        form.fillEndTime("12:00");
        form.submit();

        // Segunda franja en otro dÃ­a: Martes 14:00 - 17:00 (no se solapa)
        form.fillDay("Martes");
        form.fillStartTime("14:00");
        form.fillEndTime("17:00");
        form.submit();

        assertThat(form.isSuccess()).isTrue();
        assertThat(form.successMessage()).isNotBlank();
    }

    @Test
    @DisplayName("HU-10: el trabajador no puede configurar horarios sin sesiÃ³n")
    void sinSesionNoPuedeConfigurarHorarios() {
        driver.get(E2EConfig.baseUrl() + "/trabajador/perfil/disponibilidad");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
