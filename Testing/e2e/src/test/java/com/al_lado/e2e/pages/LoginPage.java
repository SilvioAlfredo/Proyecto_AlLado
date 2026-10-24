package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Pagina de inicio de sesion ({@code /login}) y acceso administrativo
 * ({@code /acceso-admin}). Ambas reutilizan el mismo componente
 * {@code LoginForm}, por lo que comparten selectores.
 *
 * <p>El boton de envio se busca dentro del formulario: la barra de navegacion
 * tambien tiene botones con la propiedad {@code type} resuelta a
 * {@code submit}, aunque no son el boton del formulario.</p>
 */
public class LoginPage extends BasePage {

    public static final String PATH = "/login";
    public static final String ADMIN_PATH = "/acceso-admin";

    private static final By EMAIL = By.id("login-email");
    private static final By PASSWORD = By.id("login-password");
    private static final By SUBMIT = By.cssSelector("form button[type='submit']");
    private static final By ERROR = By.cssSelector("[role='alert']");
    private static final By TOGGLE_PASSWORD = By.cssSelector("button[aria-label='Mostrar contraseña']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        navigateTo(PATH);
        Waits.visible(driver, EMAIL);
        return this;
    }

    public LoginPage openAdminAccess() {
        navigateTo(ADMIN_PATH);
        Waits.visible(driver, EMAIL);
        return this;
    }

    public LoginPage typeEmail(String email) {
        WebElement field = Waits.visible(driver, EMAIL);
        field.clear();
        field.sendKeys(email);
        return this;
    }

    public LoginPage typePassword(String password) {
        WebElement field = Waits.visible(driver, PASSWORD);
        field.clear();
        field.sendKeys(password);
        return this;
    }

    public LoginPage submit() {
        Waits.clickable(driver, SUBMIT).click();
        return this;
    }

    /**
     * Carga las credenciales y envia el formulario.
     *
     * <p>Espera el desenlace del envio: la redireccion al panel del rol cuando
     * las credenciales son validas, o la aparicion del mensaje de error cuando
     * no lo son. Sin esta espera la asercion seria aun mas rapida que la
     * respuesta del backend.</p>
     */
    public LoginPage login(String email, String password) {
        typeEmail(email).typePassword(password).submit();
        waitForOutcome();
        return this;
    }

    /** Espera a que el envio se resuelva: redireccion o mensaje de error. */
    public void waitForOutcome() {
        Waits.wait(driver).until(d -> {
            String url = d.getCurrentUrl();
            boolean redirected = !url.endsWith("/login") && !url.endsWith("/acceso-admin");
            boolean failed = Waits.isPresent(d, ERROR);
            return redirected || failed;
        });
    }

    /** Toggles the visibility of the password field. */
    public LoginPage togglePasswordVisibility() {
        Waits.clickable(driver, TOGGLE_PASSWORD).click();
        return this;
    }

    /** Text of the error banner shown by the form, or {@code null} if there is none. */
    public String errorMessage() {
        if (!Waits.isPresent(driver, ERROR)) {
            return null;
        }
        return Waits.visible(driver, ERROR).getText();
    }

    /** Checks whether the error banner is being displayed. */
    public boolean hasError() {
        return Waits.isVisible(driver, ERROR);
    }

    /** Type attribute of the password field, used to verify the visibility toggle. */
    public String passwordFieldType() {
        return Waits.visible(driver, PASSWORD).getAttribute("type");
    }

    /** Verifies that the session token was persisted in the browser storage. */
    public boolean hasStoredSession() {
        return (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return !!window.localStorage.getItem('auth_token');");
    }

    /** Roles stored in the browser storage after logging in. */
    public String storedRoles() {
        Object roles = ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return window.localStorage.getItem('user_roles');");
        return roles == null ? "" : roles.toString();
    }
}
