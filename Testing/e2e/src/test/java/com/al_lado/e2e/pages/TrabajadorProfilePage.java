package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Página de perfil de trabajador ({@code /trabajador/perfil}).
 *
 * <p>Permite al empleador o a cualquier usuario ver el perfil detallado de un
 * trabajador: fotografía, biografía, certificados, especialidades y
 * valoraciones recibidas (HU-03).</p>
 */
public class TrabajadorProfilePage extends BasePage {

    public static final String PATH = "/trabajador/perfil";

    private static final By TITLE = By.cssSelector("h1");
    private static final By NOMBRE = By.cssSelector("[data-testid='perfil-nombre']");
    private static final By BIOGRAFIA = By.cssSelector("[data-testid='perfil-biografia']");
    private static final By CERTIFICADOS = By.cssSelector("[data-testid='perfil-certificados']");
    private static final By ESPECIALIDADES = By.cssSelector("[data-testid='perfil-especialidades']");
    private static final By VALORACIONES = By.cssSelector("[data-testid='perfil-valoraciones']");
    private static final By PROMEDIO = By.cssSelector("[data-testid='perfil-promedio']");

    public TrabajadorProfilePage(WebDriver driver) {
        super(driver);
    }

    public TrabajadorProfilePage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public String nombre() {
        return Waits.visible(driver, NOMBRE).getText();
    }

    public String biografia() {
        return Waits.visible(driver, BIOGRAFIA).getText();
    }

    public String certificados() {
        return Waits.visible(driver, CERTIFICADOS).getText();
    }

    public String especialidades() {
        return Waits.visible(driver, ESPECIALIDADES).getText();
    }

    public int valoracionesCount() {
        return driver.findElements(VALORACIONES).size();
    }

    public String promedio() {
        return Waits.visible(driver, PROMEDIO).getText();
    }
}
