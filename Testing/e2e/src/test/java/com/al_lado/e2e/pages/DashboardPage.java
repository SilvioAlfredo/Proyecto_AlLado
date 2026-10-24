package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.al_lado.e2e.core.Waits;

/**
 * Paneles de cada rol: trabajador, empleador y administracion.
 *
 * <p>Cada panel se identifica por el titulo principal ({@code h1}), que es el
 * oraculo mas estable para verificar la redireccion tras iniciar sesion.</p>
 */
public class DashboardPage extends BasePage {

    public static final String WORKER_PATH = "/trabajador/dashboard";
    public static final String EMPLOYER_PATH = "/empleador/dashboard";
    public static final String ADMIN_PATH = "/admin/dashboard";

    private static final By TITLE = By.cssSelector("h1");
    private static final By STATUS = By.cssSelector("[role='status']");
    private static final By ALERT = By.cssSelector("[role='alert']");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage open(String path) {
        open(path);
        Waits.visible(driver, TITLE);
        return this;
    }

    public String heading() {
        return Waits.visible(driver, TITLE).getText();
    }

    public String errorMessage() {
        if (!Waits.isPresent(driver, ALERT)) {
            return null;
        }
        return Waits.visible(driver, ALERT).getText();
    }

    /** El panel de administracion muestra sus metricas al cargar. */
    public boolean hasMetricsLoaded() {
        return Waits.isPresent(driver, TITLE);
    }
}
