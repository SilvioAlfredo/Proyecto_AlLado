package com.al_lado.e2e.tests.empleador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.PerfilEmpresaPage;

/**
 * Casos de perfil empresarial y Sello de Confianza (area HU-14).
 *
 * <p>Verifica que un empleador puede publicar el perfil de su comercio y
 * obtener el Sello de Confianza.</p>
 */
@DisplayName("HU-14 - Perfil de comercio y Sello de Confianza")
class PerfilEmpresaTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-14 CA-01: el empleador publica el perfil de su comercio")
    void empleadorPublicaPerfilEmpresa() {
        loginAsEmployer();

        PerfilEmpresaPage perfil = new PerfilEmpresaPage(driver).open();
        perfil.fillNombreEmpresa("Mi Comercio Test");
        perfil.fillCuit("20-12345678-9");
        perfil.fillDescripcion("Descripción de mi comercio test");
        perfil.fillSitioWeb("https://comerciotest.com");
        perfil.submit();

        assertThat(perfil.isSuccess()).isTrue();
        assertThat(perfil.successMessage()).isNotBlank();
    }

    @Test
    @DisplayName("HU-14: sin sesión no se puede publicar el perfil de empresa")
    void sinSesionNoPuedePublicarPerfilEmpresa() {
        driver.get(E2EConfig.baseUrl() + "/empleador/empresa");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
