package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Página de suscripción PRO ({@code /empleador/plan} y
 * {@code /trabajador/perfil/suscripcion}).
 *
 * <p>Permite al usuario adquirir el nivel PRO realizando un aporte por
 * transferencia (HU-16).</p>
 */
public class SuscripcionPage extends BasePage {

    public static final String PATH_EMPLEADOR = "/empleador/plan";
    public static final String PATH_TRABAJADOR = "/trabajador/perfil/suscripcion";

    private static final By TITLE = By.cssSelector("h1");
    private static final By PLAN_ACTUAL = By.cssSelector("[data-testid='suscripcion-plan-actual']");
    private static final By ESTADO = By.cssSelector("[data-testid='suscripcion-estado']");
    private static final By BOTON_ADQUIRIR_PRO = By.cssSelector("[data-testid='suscripcion-adquirir-pro']");
    private static final By MONTO = By.cssSelector("[data-testid='suscripcion-monto']");
    private static final By BANCO = By.cssSelector("[data-testid='suscripcion-banco']");
    private static final By ASUNTO = By.cssSelector("[data-testid='suscripcion-asunto']");
    private static final By BOTON_CONFIRMAR = By.cssSelector("form button[type='submit']");
    private static final By ERROR = By.cssSelector("[role='alert']");
    private static final By SUCCESS = By.cssSelector("[role='status']");

    public SuscripcionPage(WebDriver driver) {
        super(driver);
    }

    public SuscripcionPage openEmpleador() {
        navigateTo(PATH_EMPLEADOR);
        Waits.visible(driver, TITLE);
        return this;
    }

    public SuscripcionPage openTrabajador() {
        navigateTo(PATH_TRABAJADOR);
        Waits.visible(driver, TITLE);
        return this;
    }

    public String planActual() {
        return Waits.visible(driver, PLAN_ACTUAL).getText();
    }

    public String estado() {
        return Waits.visible(driver, ESTADO).getText();
    }

    public SuscripcionPage clickAdquirirPro() {
        Waits.clickable(driver, BOTON_ADQUIRIR_PRO).click();
        return this;
    }

    public SuscripcionPage fillMonto(String monto) {
        WebElement field = Waits.visible(driver, MONTO);
        field.clear();
        field.sendKeys(monto);
        return this;
    }

    public SuscripcionPage fillBanco(String banco) {
        WebElement field = Waits.visible(driver, BANCO);
        field.clear();
        field.sendKeys(banco);
        return this;
    }

    public SuscripcionPage fillAsunto(String asunto) {
        WebElement field = Waits.visible(driver, ASUNTO);
        field.clear();
        field.sendKeys(asunto);
        return this;
    }

    public SuscripcionPage submit() {
        Waits.clickable(driver, BOTON_CONFIRMAR).click();
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
