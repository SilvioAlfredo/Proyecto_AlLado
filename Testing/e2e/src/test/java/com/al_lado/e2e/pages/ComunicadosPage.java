package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Página de comunicados institucionales ({@code /admin/comunicados}).
 *
 * <p>Permite al usuario leer los comunicados institucionales de la plataforma
 * (HU-20).</p>
 */
public class ComunicadosPage extends BasePage {

    public static final String PATH = "/admin/comunicados";

    private static final By TITLE = By.cssSelector("h1");
    private static final By COMUNICADOS_LISTA = By.cssSelector("[data-testid='comunicado-fila']");
    private static final By TITULO = By.cssSelector("[data-testid='comunicado-titulo']");
    private static final By CONTENIDO = By.cssSelector("[data-testid='comunicado-contenido']");

    public ComunicadosPage(WebDriver driver) {
        super(driver);
    }

    public ComunicadosPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public int comunicadosCount() {
        return driver.findElements(COMUNICADOS_LISTA).size();
    }

    public String titulo(int indice) {
        return driver.findElements(TITULO).get(indice).getText();
    }

    public String contenido(int indice) {
        return driver.findElements(CONTENIDO).get(indice).getText();
    }
}
