package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * PÃ¡gina de moderaciÃ³n de reportes ({@code /admin/moderaciÃ³n}).
 *
 * <p>Permite al administrador listar reportes pendientes y resolverlos
 * indicando un estado final.</p>
 */
public class AdminReportsPage extends BasePage {

    public static final String PATH = "/admin/moderaciÃ³n";

    private static final By TITLE = By.cssSelector("h1");
    private static final By REPORT_ROWS = By.cssSelector("[data-testid='reporte-fila']");
    private static final By RESOLVE_BUTTON = By.xpath("//button[contains(normalize-space(),'Resolver')]");
    private static final By RESOLVE_STATUS_SELECT = By.id("reporte-estado");
    private static final By CONFIRM_RESOLVE = By.cssSelector("form button[type='submit']");
    private static final By ERROR = By.cssSelector("[role='alert']");
    private static final By SUCCESS = By.cssSelector("[role='status']");

    public AdminReportsPage(WebDriver driver) {
        super(driver);
    }

    public AdminReportsPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public int reportCount() {
        return driver.findElements(REPORT_ROWS).size();
    }

    public AdminReportsPage openResolveDialog(int index) {
        driver.findElements(RESOLVE_BUTTON).get(index).click();
        return this;
    }

    public AdminReportsPage selectResolveStatus(String estado) {
        WebElement select = Waits.visible(driver, RESOLVE_STATUS_SELECT);
        new org.openqa.selenium.support.ui.Select(select).selectByVisibleText(estado);
        return this;
    }

    public AdminReportsPage confirmResolve() {
        Waits.clickable(driver, CONFIRM_RESOLVE).click();
        return this;
    }

    public boolean isSuccess() {
        return Waits.isVisible(driver, SUCCESS);
    }

    public String successMessage() {
        if (!Waits.isPresent(driver, SUCCESS)) {
            return null;
        }
        return Waits.visible(driver, SUCCESS).getText();
    }

    public boolean isError() {
        return Waits.isVisible(driver, ERROR);
    }

    public String errorMessage() {
        if (!Waits.isPresent(driver, ERROR)) {
            return null;
        }
        return Waits.visible(driver, ERROR).getText();
    }
}
