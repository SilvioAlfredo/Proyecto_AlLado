package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Página de perfil empresarial ({@code /empleador/empresa}).
 *
 * <p>Permite al empleador publicar el perfil de su comercio y obtener el
 * Sello de Confianza (HU-14).</p>
 */
public class PerfilEmpresaPage extends BasePage {

    public static final String PATH = "/empleador/empresa";

    private static final By TITLE = By.cssSelector("h1");
    private static final By NOMBRE_EMPRESA = By.cssSelector("[data-testid='perfil-nombre-empresa']");
    private static final By CUIT = By.cssSelector("[data-testid='perfil-cuit']");
    private static final By DESCRIPCION = By.cssSelector("[data-testid='perfil-descripcion']");
    private static final By SITIO_WEB = By.cssSelector("[data-testid='perfil-sitio-web']");
    private static final By LOGO = By.cssSelector("[data-testid='perfil-logo']");
    private static final By VERIFICADO = By.cssSelector("[data-testid='perfil-verificado']");
    private static final By SELLO_CONFIANZA = By.cssSelector("[data-testid='perfil-sello-confianza']");
    private static final By BOTON_GUARDAR = By.cssSelector("form button[type='submit']");
    private static final By ERROR = By.cssSelector("[role='alert']");
    private static final By SUCCESS = By.cssSelector("[role='status']");

    public PerfilEmpresaPage(WebDriver driver) {
        super(driver);
    }

    public PerfilEmpresaPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public PerfilEmpresaPage fillNombreEmpresa(String nombre) {
        WebElement field = Waits.visible(driver, NOMBRE_EMPRESA);
        field.clear();
        field.sendKeys(nombre);
        return this;
    }

    public PerfilEmpresaPage fillCuit(String cuit) {
        WebElement field = Waits.visible(driver, CUIT);
        field.clear();
        field.sendKeys(cuit);
        return this;
    }

    public PerfilEmpresaPage fillDescripcion(String descripcion) {
        WebElement field = Waits.visible(driver, DESCRIPCION);
        field.clear();
        field.sendKeys(descripcion);
        return this;
    }

    public PerfilEmpresaPage fillSitioWeb(String sitioWeb) {
        WebElement field = Waits.visible(driver, SITIO_WEB);
        field.clear();
        field.sendKeys(sitioWeb);
        return this;
    }

    public PerfilEmpresaPage submit() {
        Waits.clickable(driver, BOTON_GUARDAR).click();
        return this;
    }

    public String nombreEmpresa() {
        return Waits.visible(driver, NOMBRE_EMPRESA).getText();
    }

    public String cuit() {
        return Waits.visible(driver, CUIT).getText();
    }

    public boolean tieneSelloConfianza() {
        return Waits.isPresent(driver, SELLO_CONFIANZA);
    }

    public boolean estaVerificado() {
        return Waits.isPresent(driver, VERIFICADO);
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
