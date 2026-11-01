package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * PÃ¡gina de creaciÃ³n de oferta de empleo ({@code /empleador/solicitudes/nueva}).
 *
 * <p>Permite al empleador publicar una nueva oferta indicando tÃ­tulo,
 * descripciÃ³n, ubicaciÃ³n, modalidad, rango salarial y etiquetas.</p>
 */
public class EmployerCreateOfferPage extends BasePage {

    public static final String PATH = "/empleador/solicitudes/nueva";

    private static final By TITLE = By.cssSelector("h1");
    private static final By FIELD_TITLE = By.id("empleador-solicitud-titulo");
    private static final By FIELD_CONTENT = By.id("empleador-solicitud-contenido");
    private static final By FIELD_LOCATION = By.id("empleador-solicitud-ubicacion");
    private static final By FIELD_MODALITY = By.id("empleador-solicitud-modalidad");
    private static final By FIELD_SALARY = By.id("empleador-solicitud-rangoSalarial");
    private static final By FIELD_TAGS = By.id("empleador-solicitud-etiquetas");
    private static final By SUBMIT = By.cssSelector("form button[type='submit']");
    private static final By ERROR = By.cssSelector("[role='alert']");
    private static final By SUCCESS = By.cssSelector("[role='status']");

    public EmployerCreateOfferPage(WebDriver driver) {
        super(driver);
    }

    public EmployerCreateOfferPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public EmployerCreateOfferPage fillTitle(String titulo) {
        WebElement field = Waits.visible(driver, FIELD_TITLE);
        field.clear();
        field.sendKeys(titulo);
        return this;
    }

    public EmployerCreateOfferPage fillContent(String contenido) {
        WebElement field = Waits.visible(driver, FIELD_CONTENT);
        field.clear();
        field.sendKeys(contenido);
        return this;
    }

    public EmployerCreateOfferPage fillLocation(String ubicacion) {
        WebElement field = Waits.visible(driver, FIELD_LOCATION);
        field.clear();
        field.sendKeys(ubicacion);
        return this;
    }

    public EmployerCreateOfferPage fillModality(String modalidad) {
        WebElement field = Waits.visible(driver, FIELD_MODALITY);
        field.clear();
        field.sendKeys(modalidad);
        return this;
    }

    public EmployerCreateOfferPage fillSalaryRange(String rangoSalarial) {
        WebElement field = Waits.visible(driver, FIELD_SALARY);
        field.clear();
        field.sendKeys(rangoSalarial);
        return this;
    }

    public EmployerCreateOfferPage fillTags(String etiquetas) {
        WebElement field = Waits.visible(driver, FIELD_TAGS);
        field.clear();
        field.sendKeys(etiquetas);
        return this;
    }

    public EmployerCreateOfferPage submit() {
        Waits.clickable(driver, SUBMIT).click();
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

    public boolean isAtForm() {
        return Waits.isPresent(driver, FIELD_TITLE);
    }
}
