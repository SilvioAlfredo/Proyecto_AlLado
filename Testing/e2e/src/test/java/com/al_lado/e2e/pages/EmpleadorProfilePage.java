package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Página de perfil del empleador ({@code /empleador/perfil}).
 *
 * <p>Permite al trabajador ver el perfil del empleador que publicó una
 * necesidad: historial de oportunidades publicadas y perfil empresarial
 * (HU-05).</p>
 */
public class EmpleadorProfilePage extends BasePage {

    public static final String PATH = "/empleador/perfil";

    private static final By TITLE = By.cssSelector("h1");
    private static final By NOMBRE = By.cssSelector("[data-testid='perfil-nombre']");
    private static final By OPORTUNIDADES_PUBLICADAS = By.cssSelector("[data-testid='perfil-oportunidades']");
    private static final By PERFIL_EMPRESARIAL = By.cssSelector("[data-testid='perfil-empresarial']");
    private static final By CUIT = By.cssSelector("[data-testid='perfil-cuit']");
    private static final By SELLO_CONFIANZA = By.cssSelector("[data-testid='perfil-sello-confianza']");

    public EmpleadorProfilePage(WebDriver driver) {
        super(driver);
    }

    public EmpleadorProfilePage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public String nombre() {
        return Waits.visible(driver, NOMBRE).getText();
    }

    public int oportunidadesPublicadasCount() {
        return driver.findElements(OPORTUNIDADES_PUBLICADAS).size();
    }

    public boolean tienePerfilEmpresarial() {
        return Waits.isPresent(driver, PERFIL_EMPRESARIAL);
    }

    public String cuit() {
        return Waits.visible(driver, CUIT).getText();
    }

    public boolean tieneSelloConfianza() {
        return Waits.isPresent(driver, SELLO_CONFIANZA);
    }
}
