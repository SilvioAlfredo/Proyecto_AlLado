package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Página de favoritos del empleador ({@code /empleador/favoritos}).
 *
 * <p>Permite al empleador ver y gestionar sus trabajadores favoritos
 * (HU-11).</p>
 */
public class FavoritosPage extends BasePage {

    public static final String PATH = "/empleador/favoritos";

    private static final By TITLE = By.cssSelector("h1");
    private static final By FAVORITOS_LISTA = By.cssSelector("[data-testid='favorito-fila']");
    private static final By TRABAJADOR_NOMBRE = By.cssSelector("[data-testid='favorito-trabajador-nombre']");
    private static final By BOTON_ELIMINAR = By.cssSelector("[data-testid='favorito-eliminar']");

    public FavoritosPage(WebDriver driver) {
        super(driver);
    }

    public FavoritosPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public int favoritosCount() {
        return driver.findElements(FAVORITOS_LISTA).size();
    }

    public String trabajadorNombre(int indice) {
        return driver.findElements(TRABAJADOR_NOMBRE).get(indice).getText();
    }

    public FavoritosPage eliminarFavorito(int indice) {
        driver.findElements(BOTON_ELIMINAR).get(indice).click();
        return this;
    }
}
