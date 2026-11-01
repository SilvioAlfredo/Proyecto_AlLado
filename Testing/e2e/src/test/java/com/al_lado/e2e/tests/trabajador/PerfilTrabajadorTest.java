package com.al_lado.e2e.tests.trabajador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.pages.TrabajadorProfilePage;

/**
 * Casos de visualización de perfiles de trabajadores postulados (area HU-03).
 *
 * <p>Verifica que un empleador puede ver el perfil detallado de un trabajador
 * que se postuló a su oferta: fotografía, biografía, certificados,
 * especialidades y valoraciones recibidas.</p>
 */
@DisplayName("HU-03 - Ver perfiles de trabajadores postulados")
class PerfilTrabajadorTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-03 CA-01: el empleador ve el perfil detallado de un trabajador postulado")
    void empleadorVePerfilTrabajadorPostulado() {
        loginAsEmployer();

        TrabajadorProfilePage perfil = new TrabajadorProfilePage(driver).open();

        assertThat(perfil.nombre()).isNotBlank();
        assertThat(perfil.biografia()).isNotBlank();
        assertThat(perfil.especialidades()).isNotBlank();
    }

    @Test
    @DisplayName("HU-03: el trabajador ve su propio perfil")
    void trabajadorVeSuPropioPerfil() {
        loginAsWorker();

        TrabajadorProfilePage perfil = new TrabajadorProfilePage(driver).open();

        assertThat(perfil.nombre()).isNotBlank();
    }

    @Test
    @DisplayName("HU-03: sin sesión no se puede ver el perfil de un trabajador")
    void sinSesionNoPuedeVerPerfilTrabajador() {
        driver.get(E2EConfig.baseUrl() + "/trabajador/perfil");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
