package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * PÃ¡gina de calificaciÃ³n de contrataciÃ³n ({@code /empleador/contratos-valorar}).
 *
 * <p>Permite al empleador calificar con estrellas y dejar una reseÃ±a sobre el
 * servicio recibido de un trabajador.</p>
 */
public class EmployerRatingPage extends BasePage {

    public static final String PATH = "/empleador/contratos-valorar";

    private static final By TITLE = By.cssSelector("h1");
    private static final By STAR_1 = By.cssSelector("button[aria-label='1 estrella']");
    private static final By STAR_2 = By.cssSelector("button[aria-label='2 estrellas']");
    private static final By STAR_3 = By.cssSelector("button[aria-label='3 estrellas']");
    private static final By STAR_4 = By.cssSelector("button[aria-label='4 estrellas']");
    private static final By STAR_5 = By.cssSelector("button[aria-label='5 estrellas']");
    private static final By FIELD_COMMENT = By.id("empleador-valoracion-comentario");
    private static final By SUBMIT = By.cssSelector("form button[type='submit']");
    private static final By ERROR = By.cssSelector("[role='alert']");
    private static final By SUCCESS = By.cssSelector("[role='status']");

    public EmployerRatingPage(WebDriver driver) {
        super(driver);
    }

    public EmployerRatingPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public EmployerRatingPage rate(int estrellas) {
        By estrella = switch (estrellas) {
            case 1 -> STAR_1;
            case 2 -> STAR_2;
            case 3 -> STAR_3;
            case 4 -> STAR_4;
            case 5 -> STAR_5;
            default -> throw new IllegalArgumentException("Estrellas invÃ¡lidas: " + estrellas);
        };
        Waits.clickable(driver, estrella).click();
        return this;
    }

    public EmployerRatingPage fillComment(String comentario) {
        WebElement field = Waits.visible(driver, FIELD_COMMENT);
        field.clear();
        field.sendKeys(comentario);
        return this;
    }

    public EmployerRatingPage submit() {
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

    public boolean isAtForm() {
        return Waits.isPresent(driver, FIELD_COMMENT);
    }
}
