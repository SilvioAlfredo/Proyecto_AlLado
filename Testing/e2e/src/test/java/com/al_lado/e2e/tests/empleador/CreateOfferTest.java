package com.al_lado.e2e.tests.empleador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.EmployerCreateOfferPage;

/**
 * Casos de publicaciÃ³n de ofertas de empleo (area HU-01).
 *
 * <p>Verifica que un empleador autenticado puede publicar una oferta
 * indicando tÃ­tulo, descripciÃ³n, ubicaciÃ³n, modalidad, rango salarial y
 * etiquetas, y que el sistema la deja visible en el listado de oportunidades.</p>
 */
@DisplayName("HU-01 - Publicar ofertas de empleo")
class CreateOfferTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-01 CA-01: publicaciÃ³n exitosa de una oferta de empleo")
    void publicacionExitosaDeOferta() {
        loginAsEmployer();

        EmployerCreateOfferPage form = new EmployerCreateOfferPage(driver).open();
        form.fillTitle("ReparaciÃ³n de fuga de agua");
        form.fillContent("Necesito un plomero para arreglar una fuga en la cocina.");
        form.fillLocation("Barrio Centro");
        form.fillModality("PRESENCIAL");
        form.fillSalaryRange("8000-12000");
        form.fillTags("plomería, hogar, reparación");
        form.submit();

        assertThat(form.isSuccess()).isTrue();
        assertThat(form.successMessage()).isNotBlank();
    }

    @Test
    @DisplayName("HU-01 CA-01: campos obligatorios no vacíos")
    void camposObligatoriosNoVacios() {
        loginAsEmployer();

        EmployerCreateOfferPage form = new EmployerCreateOfferPage(driver).open();
        form.submit();

        assertThat(form.isAtForm()).isTrue();
        // El formulario se queda en la pÃ¡gina sin enviar
        assertThat(driver.getCurrentUrl()).contains("/empleador/solicitudes/nueva");
    }

    @Test
    @DisplayName("HU-01: el empleador no puede publicar sin sesiÃ³n")
    void sinSesionNoPuedePublicar() {
        driver.get(E2EConfig.baseUrl() + "/empleador/solicitudes/nueva");

        // Sin sesiÃ³n el guard redirige al login
        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
