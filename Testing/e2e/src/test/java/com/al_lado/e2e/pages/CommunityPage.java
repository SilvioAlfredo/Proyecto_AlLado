package com.al_lado.e2e.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Foro de la comunidad ({@code /trabajador/comunidad}).
 *
 * <p>Cada publicacion es un {@code <article>}; el boton "Nueva publicación" abre un
 * modal que no declara {@code type="submit"} en el disparador, por eso se localiza
 * por texto.</p>
 */
public class CommunityPage extends BasePage {

    public static final String PATH = "/trabajador/comunidad";

    private static final By TITLE = By.cssSelector("h1");
    private static final By NEW_PUBLICATION = By.xpath("//button[normalize-space()='Nueva publicación']");
    private static final By ARTICLE = By.cssSelector("article");
    private static final By ARTICLE_TITLE = By.cssSelector("article h2");
    private static final By EMPTY_STATE = By.xpath("//p[normalize-space()='Todavía no hay publicaciones.']");
    private static final By STATUS = By.cssSelector("[role='status']");
    private static final By ALERT = By.cssSelector("[role='alert']");
    private static final By MODAL_TITLE = By.xpath("//h2[normalize-space()='Crear publicación']");
    private static final By MODAL_TITULO = By.cssSelector("input[name='titulo']");
    private static final By MODAL_CONTENIDO = By.cssSelector("textarea[name='contenido']");
    private static final By MODAL_SUBMIT = By.cssSelector("form button[type='submit']");

    public CommunityPage(WebDriver driver) {
        super(driver);
    }

    public CommunityPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public String heading() {
        return Waits.visible(driver, TITLE).getText();
    }

    /** Titulos de las publicaciones visibles en el foro. */
    public List<String> publicationTitles() {
        List<WebElement> titles = driver.findElements(ARTICLE_TITLE);
        return titles.stream().map(WebElement::getText).toList();
    }

    public int publicationCount() {
        return driver.findElements(ARTICLE).size();
    }

    public boolean showsEmptyState() {
        return Waits.isPresent(driver, EMPTY_STATE);
    }

    /** Abre el modal de alta de publicacion. */
    public void openNewPublicationModal() {
        Waits.clickable(driver, NEW_PUBLICATION).click();
        Waits.visible(driver, MODAL_TITLE);
    }

    /** Crea una publicacion en el foro. */
    public void createPublication(String titulo, String contenido) {
        openNewPublicationModal();
        WebElement tituloField = Waits.visible(driver, MODAL_TITULO);
        tituloField.clear();
        tituloField.sendKeys(titulo);
        WebElement contenidoField = Waits.visible(driver, MODAL_CONTENIDO);
        contenidoField.clear();
        contenidoField.sendKeys(contenido);
        Waits.clickable(driver, MODAL_SUBMIT).click();
        // Espera a que el backend confirme el alta o devuelva un error.
        Waits.wait(driver).until(d -> Waits.isPresent(d, STATUS) || Waits.isPresent(d, ALERT));
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
