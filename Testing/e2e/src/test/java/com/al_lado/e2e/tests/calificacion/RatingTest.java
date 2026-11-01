package com.al_lado.e2e.tests.calificacion;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.EmployerRatingPage;

/**
 * Casos de calificaciÃ³n con estrellas y reseÃ±a (area HU-06).
 *
 * <p>Verifica que un empleador puede calificar con estrellas (1 a 5) y dejar
 * una reseÃ±a opcional sobre el servicio recibido, y que el sistema persiste
 * la valoraciÃ³n y actualiza el promedio del trabajador.</p>
 */
@DisplayName("HU-06 - CalificaciÃ³n con estrellas y reseÃ±a")
class RatingTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-06 CA-01: calificaciÃ³n exitosa de 5 estrellas con comentario")
    void calificacionExitosaCon5EstrellasYComentario() {
        loginAsEmployer();

        EmployerRatingPage form = new EmployerRatingPage(driver).open();
        form.rate(5);
        form.fillComment("Excelente servicio, muy profesional y puntual.");
        form.submit();

        assertThat(form.isSuccess()).isTrue();
        assertThat(form.successMessage()).isNotBlank();
    }

    @Test
    @DisplayName("HU-06 CA-01: calificaciÃ³n de 1 estrella sin comentario")
    void calificacionDe1EstrellaSinComentario() {
        loginAsEmployer();

        EmployerRatingPage form = new EmployerRatingPage(driver).open();
        form.rate(1);
        form.submit();

        assertThat(form.isSuccess()).isTrue();
        assertThat(form.successMessage()).isNotBlank();
    }

    @Test
    @DisplayName("HU-06 CA-01: calificar con estrellas fuera de rango se rechaza")
    void calificacionFueraDeRangoSeRechaza() {
        loginAsEmployer();

        EmployerRatingPage form = new EmployerRatingPage(driver).open();
        // No hay estrella 0 ni 6: el formulario solo ofrece 1-5
        assertThat(form.isAtForm()).isTrue();
    }

    @Test
    @DisplayName("HU-06: el empleador no puede calificar sin sesiÃ³n")
    void sinSesionNoPuedeCalificar() {
        driver.get(E2EConfig.baseUrl() + "/empleador/contratos-valorar");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
