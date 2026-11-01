package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Página de anuncios de comercio ({@code /empleador/anuncios}).
 *
 * <p>Permite al empleador publicar anuncios de su comercio en la plataforma
 * (HU-15).</p>
 */
public class PublicidadPage extends BasePage {

    public static final String PATH = "/empleador/anuncios";

    private static final By TITLE = By.cssSelector("h1");
    private static final By ANUNCIOS_LISTA = By.cssSelector("[data-testid='anuncio-fila']");
    private static final By TITULO = By.cssSelector("[data-testid='anuncio-titulo']");
    private static final By BOTON_CREAR = By.cssSelector("[data-testid='anuncio-crear']");
    private static final By ERROR = By.cssSelector("[role='alert']");
    private static final By SUCCESS = By.cssSelector("[role='status']");

    public PublicidadPage(WebDriver driver) {
        super(driver);
    }

    public PublicidadPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public int anunciosCount() {
        return driver.findElements(ANUNCIOS_LISTA).size();
    }

    public String titulo(int indice) {
        return driver.findElements(TITULO).get(indice).getText();
    }

    public PublicidadPage clickCrear() {
        Waits.clickable(driver, BOTON_CREAR).click();
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
