package com.al_lado.e2e.pages;

import org.openqa.selenium.WebDriver;

import com.al_lado.e2e.core.E2EConfig;

/**
 * Base de los page objects: conoce el driver activo y la raiz de la aplicacion.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final String baseUrl;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.baseUrl = E2EConfig.baseUrl();
    }

    /** Navigates to an application route (for example {@code /login}). */
    protected void navigateTo(String path) {
        driver.get(baseUrl + path);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String title() {
        return driver.getTitle();
    }
}
