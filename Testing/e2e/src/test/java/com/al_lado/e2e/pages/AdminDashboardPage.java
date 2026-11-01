package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.al_lado.e2e.core.Waits;

/**
 * PÃ¡gina del panel de mÃ©tricas de administraciÃ³n ({@code /admin/dashboard}).
 *
 * <p>Muestra indicadores clave del platforma: contratos, trabajadores,
 * ingresos, postulaciones por estado, etc.</p>
 */
public class AdminDashboardPage extends BasePage {

    public static final String PATH = "/admin/dashboard";

    private static final By TITLE = By.cssSelector("h1");
    private static final By METRIC_CONTRACTS = By.cssSelector("[data-testid='metrica-contratos']");
    private static final By METRIC_WORKERS = By.cssSelector("[data-testid='metrica-trabajadores']");
    private static final By METRIC_INCOME = By.cssSelector("[data-testid='metrica-ingresos']");
    private static final By METRIC_COMPLETED = By.cssSelector("[data-testid='metrica-completados']");
    private static final By ALERT = By.cssSelector("[role='alert']");

    public AdminDashboardPage(WebDriver driver) {
        super(driver);
    }

    public AdminDashboardPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public String metricContracts() {
        return Waits.visible(driver, METRIC_CONTRACTS).getText();
    }

    public String metricWorkers() {
        return Waits.visible(driver, METRIC_WORKERS).getText();
    }

    public String metricIncome() {
        return Waits.visible(driver, METRIC_INCOME).getText();
    }

    public String metricCompleted() {
        return Waits.visible(driver, METRIC_COMPLETED).getText();
    }

    public boolean isError() {
        return Waits.isVisible(driver, ALERT);
    }

    public String errorMessage() {
        if (!Waits.isPresent(driver, ALERT)) {
            return null;
        }
        return Waits.visible(driver, ALERT).getText();
    }
}
