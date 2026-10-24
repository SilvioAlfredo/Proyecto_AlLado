package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Modal de postulacion a una oportunidad.
 *
 * <p>Los tres campos no estan asociados a un {@code label} (el texto del label no
 * envuelve al control), por eso se localizan por {@code placeholder} y por
 * atributos: remuneracion {@code input[placeholder='Ej. 18000']}, plazo
 * {@code input[type='number'][min='1']} y mensaje el {@code textarea}.</p>
 */
public class PostulationModal extends BasePage {

    private static final By HEADING = By.xpath("//h3[normalize-space()='Enviar Postulación']");
    private static final By REMUNERATION = By.cssSelector("input[placeholder='Ej. 18000']");
    private static final By DAYS = By.cssSelector("input[type='number'][min='1']");
    private static final By MESSAGE = By.cssSelector("textarea[placeholder^='Explica tu experiencia previa']");
    private static final By SUBMIT = By.cssSelector("form button[type='submit']");
    private static final By ERROR = By.cssSelector("div.text-red-300 span");
    private static final By VALIDATION_ERROR = By.cssSelector("div[class*='border-red-400/30'] span");
    private static final By STATUS = By.cssSelector("[role='status']");

    public PostulationModal(WebDriver driver) {
        super(driver);
    }

    /** Espera a que el modal este montado. */
    public PostulationModal waitUntilVisible() {
        Waits.visible(driver, HEADING);
        return this;
    }

    public PostulationModal fill(String remuneracion, String dias, String mensaje) {
        fill(REMUNERATION, remuneracion);
        fill(DAYS, dias);
        fill(MESSAGE, mensaje);
        return this;
    }

    /**
     * Vacia un campo de texto de forma que el framework de la aplicacion (React)
     * registre el cambio.
     *
     * <p>{@code WebElement.clear()} no siempre dispara el evento que React
     * escucha, con lo que el estado interno conserva el valor anterior. Borrar
     * con el teclado (seleccionar todo y eliminar) si lo hace.</p>
     */
    public PostulationModal clearMensaje() {
        WebElement campo = Waits.visible(driver, MESSAGE);
        campo.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        campo.sendKeys(Keys.DELETE);
        return this;
    }

    public PostulationModal submit() {
        Waits.clickable(driver, SUBMIT).click();
        return this;
    }

    /**
     * Espera el desenlace del envio: un mensaje de error (validacion del
     * formulario o del backend) o la confirmacion de la postulacion.
     *
     * <p>El elemento {@code [role='status']} se compara por texto porque la
     * pantalla de detalle tambien lo usa para el mensaje de carga
     * ("Cargando oportunidad..."), que no es un desenlace.</p>
     *
     * @return {@code false} si no hubo desenlace: el navegador bloqueo el envio
     *         por validacion HTML5 (por ejemplo, un campo numerico obligatorio vacio)
     */
    public boolean waitForOutcome() {
        return Waits.waitUntil(driver, java.time.Duration.ofSeconds(8),
                d -> Waits.isPresent(d, ERROR)
                        || Waits.isPresent(d, VALIDATION_ERROR)
                        || confirmacionVisible(d)
                        || d.getCurrentUrl().contains("/postulaciones"));
    }

    /** El banner de exito de la postulacion. */
    public String statusMessage() {
        if (!Waits.isPresent(driver, STATUS)) {
            return null;
        }
        return Waits.visible(driver, STATUS).getText();
    }

    /** Mensaje de error del backend o de la validacion del formulario. */
    public String errorMessage() {
        if (Waits.isPresent(driver, ERROR)) {
            return Waits.visible(driver, ERROR).getText();
        }
        if (Waits.isPresent(driver, VALIDATION_ERROR)) {
            return Waits.visible(driver, VALIDATION_ERROR).getText();
        }
        return null;
    }

    /** El banner visible es el de exito y no el de carga. */
    private boolean confirmacionVisible(WebDriver driver) {
        if (!Waits.isPresent(driver, STATUS)) {
            return false;
        }
        return driver.findElement(STATUS).getText().toLowerCase().contains("enviad");
    }

    private void fill(By locator, String value) {
        WebElement field = Waits.visible(driver, locator);
        field.clear();
        field.sendKeys(value);
    }
}
