package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Formulario de alta de cuentas ({@code /register}).
 *
 * <p>El selector de rol no usa radios sino botones con {@code aria-pressed}, y los
 * cuatro campos de datos personales no tienen {@code id}: se localizan por su
 * {@code placeholder}.</p>
 */
public class RegisterPage extends BasePage {

    public static final String PATH = "/register";

    private static final By ROLE_TRABAJADOR = By.xpath(
            "//button[.//span[normalize-space()='Quiero trabajar']]");
    private static final By ROLE_EMPLEADOR = By.xpath(
            "//button[.//span[normalize-space()='Quiero contratar']]");
    private static final By NAME = By.cssSelector("input[placeholder='Tu nombre']");
    private static final By LAST_NAME = By.cssSelector("input[placeholder='Tu apellido']");
    private static final By EMAIL = By.cssSelector("input[placeholder='nombre@ejemplo.com']");
    private static final By PHONE = By.cssSelector("input[placeholder='Ej.: +54 351 555 1234']");
    private static final By PASSWORD = By.id("register-password");
    private static final By PASSWORD_HELP = By.id("register-password-help");
    private static final By SUBMIT = By.cssSelector("button[type='submit']");
    private static final By STATUS = By.cssSelector("[role='status']");
    private static final By ALERT = By.cssSelector("[role='alert']");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public RegisterPage open() {
        navigateTo(PATH);
        Waits.visible(driver, NAME);
        return this;
    }

    public RegisterPage open(String queryRole) {
        navigateTo(PATH + "?rol=" + queryRole);
        Waits.visible(driver, NAME);
        return this;
    }

    /** Elige el rol de la cuenta. */
    public RegisterPage selectWorkerRole() {
        Waits.clickable(driver, ROLE_TRABAJADOR).click();
        return this;
    }

    public RegisterPage selectEmployerRole() {
        Waits.clickable(driver, ROLE_EMPLEADOR).click();
        return this;
    }

    /** Indica si el boton del rol indicado quedo seleccionado. */
    public boolean isRoleSelected(String role) {
        By locator = "EMPLEADOR".equals(role) ? ROLE_EMPLEADOR : ROLE_TRABAJADOR;
        return "true".equals(Waits.visible(driver, locator).getAttribute("aria-pressed"));
    }

    public RegisterPage fillPersonalData(String nombre, String apellido, String email, String telefono) {
        fill(NAME, nombre);
        fill(LAST_NAME, apellido);
        fill(EMAIL, email);
        fill(PHONE, telefono);
        return this;
    }

    public RegisterPage fillPassword(String password) {
        fill(PASSWORD, password);
        return this;
    }

    public RegisterPage submit() {
        Waits.clickable(driver, SUBMIT).click();
        return this;
    }

    /**
     * Espera el desenlace del alta.
     *
     * @return {@code true} si el backend respondio (alta correcta o error) y
     *         {@code false} si el navegador bloqueo el envio por validacion
     *         HTML5, que es el caso de una contrasena de menos de 8 caracteres
     */
    public boolean waitForOutcome() {
        return Waits.waitUntil(driver, java.time.Duration.ofSeconds(5),
                d -> Waits.isPresent(d, STATUS) || Waits.isPresent(d, ALERT));
    }

    /** Mensaje de exito tras crear la cuenta. */
    public String successMessage() {
        if (!Waits.isPresent(driver, STATUS)) {
            return null;
        }
        return Waits.visible(driver, STATUS).getText();
    }

    /** Mensaje de error devuelto por el backend. */
    public String errorMessage() {
        if (!Waits.isPresent(driver, ALERT)) {
            return null;
        }
        return Waits.visible(driver, ALERT).getText();
    }

    /** Texto de ayuda de la contrasena (regla de longitud minima). */
    public String passwordHelp() {
        return Waits.visible(driver, PASSWORD_HELP).getText();
    }

    /**
     * Indica si el navegador bloqueara el envio por validacion HTML5.
     * El campo de contrasena declara {@code minlength="8"}, asi que una
     * contrasena mas corta no llega al backend.
     */
    public boolean isPasswordLongEnough(String password) {
        return password != null && password.length() >= 8;
    }

    /** Texto del boton de envio; cambia a "Cuenta creada" cuando la alta fue correcta. */
    public String submitButtonText() {
        return Waits.visible(driver, SUBMIT).getText();
    }

    private void fill(By locator, String value) {
        WebElement field = Waits.visible(driver, locator);
        field.clear();
        field.sendKeys(value);
    }
}
