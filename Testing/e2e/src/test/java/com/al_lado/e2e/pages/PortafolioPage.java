package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Página de portafolio del trabajador ({@code /trabajador/perfil/portafolio}).
 *
 * <p>Permite al trabajador armar su portafolio y acceder a las propuestas
 * reservadas para usuarios PRO (HU-17).</p>
 */
public class PortafolioPage extends BasePage {

    public static final String PATH = "/trabajador/perfil/portafolio";

    private static final By TITLE = By.cssSelector("h1");
    private static final By PORTAFOLIO_ITEMS = By.cssSelector("[data-testid='portafolio-item']");
    private static final By TITULO_ITEM = By.cssSelector("[data-testid='portafolio-titulo']");
    private static final By BOTON_AGREGAR = By.cssSelector("[data-testid='portafolio-agregar']");
    private static final By ERROR = By.cssSelector("[role='alert']");
    private static final By SUCCESS = By.cssSelector("[role='status']");

    public PortafolioPage(WebDriver driver) {
        super(driver);
    }

    public PortafolioPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public int itemsCount() {
        return driver.findElements(PORTAFOLIO_ITEMS).size();
    }

    public String tituloItem(int indice) {
        return driver.findElements(TITULO_ITEM).get(indice).getText();
    }

    public PortafolioPage clickAgregar() {
        Waits.clickable(driver, BOTON_AGREGAR).click();
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
