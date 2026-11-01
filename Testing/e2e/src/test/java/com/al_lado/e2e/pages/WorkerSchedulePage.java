package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * PÃ¡gina de configuraciÃ³n de horarios de disponibilidad ({@code /trabajador/perfil/disponibilidad}).
 *
 * <p>Permite al trabajador agregar franjas horarias indicando dÃ­a, hora de
 * inicio y hora de fin.</p>
 */
public class WorkerSchedulePage extends BasePage {

    public static final String PATH = "/trabajador/perfil/disponibilidad";

    private static final By TITLE = By.cssSelector("h1");
    private static final By FIELD_DAY = By.id("horario-diaSemana");
    private static final By FIELD_START = By.id("horario-horaInicio");
    private static final By FIELD_END = By.id("horario-horaFin");
    private static final By SUBMIT = By.cssSelector("form button[type='submit']");
    private static final By ERROR = By.cssSelector("[role='alert']");
    private static final By SUCCESS = By.cssSelector("[role='status']");
    private static final By SCHEDULE_LIST = By.cssSelector("[data-testid='horario-item']");

    public WorkerSchedulePage(WebDriver driver) {
        super(driver);
    }

    public WorkerSchedulePage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public WorkerSchedulePage fillDay(String diaSemana) {
        WebElement field = Waits.visible(driver, FIELD_DAY);
        field.clear();
        field.sendKeys(diaSemana);
        return this;
    }

    public WorkerSchedulePage fillStartTime(String horaInicio) {
        WebElement field = Waits.visible(driver, FIELD_START);
        field.clear();
        field.sendKeys(horaInicio);
        return this;
    }

    public WorkerSchedulePage fillEndTime(String horaFin) {
        WebElement field = Waits.visible(driver, FIELD_END);
        field.clear();
        field.sendKeys(horaFin);
        return this;
    }

    public WorkerSchedulePage submit() {
        Waits.clickable(driver, SUBMIT).click();
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

    public int scheduleCount() {
        return driver.findElements(SCHEDULE_LIST).size();
    }
}
