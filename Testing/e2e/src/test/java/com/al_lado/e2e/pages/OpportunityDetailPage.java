package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.al_lado.e2e.core.Waits;

/**
 * Detalle de una oportunidad ({@code /trabajador/oportunidades/{id}}).
 *
 * <p>El boton "Postularme ahora" abre un modal en la misma ruta; la variante
 * {@code /trabajador/oportunidades/{id}/postular} renderiza ese mismo modal en su
 * propia URL.</p>
 */
public class OpportunityDetailPage extends BasePage {

    private static final By TITLE = By.cssSelector("h1");
    private static final By APPLY_BUTTON = By.xpath(
            "//button[contains(normalize-space(),'Postularme ahora')]");
    private static final By CLOSED_BUTTON = By.xpath("//button[contains(normalize-space(),'Oferta cerrada')]");
    private static final By ALREADY_APPLIED = By.xpath("//button[contains(normalize-space(),'Enviada')]");
    private static final By STATUS = By.cssSelector("[role='status']");
    private static final By ALERT = By.cssSelector("[role='alert']");

    public OpportunityDetailPage(WebDriver driver) {
        super(driver);
    }

    public OpportunityDetailPage open(long id) {
        navigateTo("/trabajador/oportunidades/" + id);
        Waits.visible(driver, TITLE);
        return this;
    }

    public String title() {
        return Waits.visible(driver, TITLE).getText();
    }

    /** Indica si la oportunidad se puede postular (esta activa y no se postulo antes). */
    public boolean canApply() {
        return Waits.isPresent(driver, APPLY_BUTTON);
    }

    /** Pulsa "Postularme ahora" y devuelve el modal de postulacion. */
    public PostulationModal openPostulationModal() {
        Waits.clickable(driver, APPLY_BUTTON).click();
        return new PostulationModal(driver);
    }

    /** Texto del boton deshabilitado cuando la oportunidad esta cerrada. */
    public String closedButtonText() {
        if (!Waits.isPresent(driver, CLOSED_BUTTON)) {
            return null;
        }
        return Waits.visible(driver, CLOSED_BUTTON).getText();
    }

    /** Texto del boton cuando el trabajador ya se postulo. */
    public String alreadyAppliedText() {
        if (!Waits.isPresent(driver, ALREADY_APPLIED)) {
            return null;
        }
        return Waits.visible(driver, ALREADY_APPLIED).getText();
    }

    public String statusMessage() {
        if (!Waits.isPresent(driver, STATUS)) {
            return null;
        }
        return Waits.visible(driver, STATUS).getText();
    }

    public String errorMessage() {
        if (!Waits.isPresent(driver, ALERT)) {
            return null;
        }
        return Waits.visible(driver, ALERT).getText();
    }
}
