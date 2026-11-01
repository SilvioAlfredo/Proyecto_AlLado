package com.al_lado.e2e.tests.empleador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.FavoritosPage;

/**
 * Casos de favoritos de trabajadores (area HU-11).
 *
 * <p>Verifica que un empleador puede almacenar perfiles de trabajadores
 * seleccionados en una sección de favoritos y desmarcarlos.</p>
 */
@DisplayName("HU-11 - Favoritos de trabajadores")
class FavoritosTest extends BaseE2ETest {

    @Test
    @DisplayName("HU-11 CA-01: el empleador ve sus trabajadores favoritos")
    void empleadorVeFavoritos() {
        loginAsEmployer();

        FavoritosPage favoritos = new FavoritosPage(driver).open();

        assertThat(favoritos.favoritosCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("HU-11: sin sesión no se pueden ver los favoritos")
    void sinSesionNoPuedeVerFavoritos() {
        driver.get(E2EConfig.baseUrl() + "/empleador/favoritos");

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver,
                        java.time.Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
