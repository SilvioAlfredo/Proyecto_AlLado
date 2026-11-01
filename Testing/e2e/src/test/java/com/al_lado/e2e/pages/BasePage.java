package com.al_lado.e2e.pages;

import org.openqa.selenium.WebDriver;

import com.al_lado.e2e.core.E2EConfig;

/**
 * Clase base de los Page Objects.
 *
 * <p>Provee atajos de navegaciÃ³n y esperas comunes para evitar repetir cÃ³digo
 * en cada pÃ¡gina.</p>
 */
public abstract class BasePage {

    protected final WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    /** Navega a una ruta relativa de la aplicaciÃ³n frontend. */
    protected void navigateTo(String path) {
        driver.get(E2EConfig.baseUrl() + path);
    }
}
