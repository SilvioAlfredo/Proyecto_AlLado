package com.al_lado.e2e.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Listado de oportunidades del trabajador ({@code /trabajador/oportunidades}).
 *
 * <p>El filtrado es de cliente: no hay llamada al backend al filtrar, salvo la
 * carga inicial via {@code POST /oportunidades/filtrar}.</p>
 *
 * <p>Cada tarjeta es un bloque que contiene el titulo (un {@code h2}) y, en una
 * zona aparte, el enlace al detalle. Por eso la tarjeta se localiza desde el
 * enlace hacia el ancestro mas cercano que tenga un {@code h2}.</p>
 */
public class OpportunitiesPage extends BasePage {

    public static final String PATH = "/trabajador/oportunidades";

    private static final By TITLE = By.cssSelector("h1");
    private static final By SEARCH = By.cssSelector("input[placeholder='Buscar por oficio o tarea...']");
    private static final By FILTERS_BUTTON = By.xpath("//button[.//span[normalize-space()='Filtros']]");
    private static final By CARD_LINK = By.cssSelector("a[href^='/trabajador/oportunidades/']");
    private static final By CARD = By.xpath(
            "//a[starts-with(@href,'/trabajador/oportunidades/')]/ancestor::div[.//h2][1]");
    private static final By EMPTY_STATE = By.xpath("//h3[normalize-space()='No se encontraron trabajos']");
    private static final By ALERT = By.cssSelector("[role='alert']");
    private static final By MODALITY_SELECT = By.xpath("//label[contains(text(),'Modalidad')]/select");
    private static final By LOCATION_INPUT = By.xpath("//label[contains(text(),'Ubicación')]/input");
    private static final By ONLY_HIGHLIGHTED = By.cssSelector("input[type='checkbox']");
    private static final By CLEAR_FILTERS = By.xpath("//button[normalize-space()='Limpiar']");
    private static final By APPLY_FILTERS = By.xpath("//button[normalize-space()='Aplicar filtros']");

    public OpportunitiesPage(WebDriver driver) {
        super(driver);
    }

    public OpportunitiesPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public String heading() {
        return Waits.visible(driver, TITLE).getText();
    }

    /** Espera a que termine la carga del listado. */
    public OpportunitiesPage waitUntilLoaded() {
        Waits.wait(driver).until(d -> d.findElements(CARD).size() > 0
                || Waits.isPresent(d, EMPTY_STATE)
                || Waits.isPresent(d, ALERT));
        return this;
    }

    /** Tarjetas de oportunidad visibles. */
    public List<WebElement> cards() {
        return driver.findElements(CARD);
    }

    /** Titulos de las tarjetas de oportunidad visibles. */
    public List<String> visibleOpportunityTitles() {
        List<String> titles = new ArrayList<>();
        for (WebElement card : cards()) {
            titles.add(card.findElement(By.tagName("h2")).getText().trim());
        }
        return titles;
    }

    public int opportunityCount() {
        return cards().size();
    }

    /** Escribe en el buscador por oficio o tarea. */
    public OpportunitiesPage search(String text) {
        WebElement field = Waits.visible(driver, SEARCH);
        field.clear();
        field.sendKeys(text);
        return this;
    }

    /** Titulos que sobreviven al filtro de busqueda actual. */
    public List<String> titlesAfterSearch(String text) {
        search(text);
        return visibleOpportunityTitles();
    }

    /** Abre el panel lateral de filtros. */
    public OpportunitiesPage openFiltersDrawer() {
        Waits.clickable(driver, FILTERS_BUTTON).click();
        Waits.visible(driver, MODALITY_SELECT);
        return this;
    }

    /** Aplica el filtro de modalidad (por ejemplo {@code PRESENCIAL}). */
    public OpportunitiesPage filterByModality(String modality) {
        openFiltersDrawer();
        WebElement select = Waits.visible(driver, MODALITY_SELECT);
        new org.openqa.selenium.support.ui.Select(select).selectByVisibleText(modality);
        Waits.clickable(driver, APPLY_FILTERS).click();
        return this;
    }

    /** Aplica el filtro de ubicacion (texto libre). */
    public OpportunitiesPage filterByLocation(String location) {
        openFiltersDrawer();
        WebElement field = Waits.visible(driver, LOCATION_INPUT);
        field.clear();
        field.sendKeys(location);
        Waits.clickable(driver, APPLY_FILTERS).click();
        return this;
    }

    /** Marca la casilla "solo destacadas". */
    public OpportunitiesPage filterOnlyHighlighted() {
        openFiltersDrawer();
        WebElement checkbox = Waits.visible(driver, ONLY_HIGHLIGHTED);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
        Waits.clickable(driver, APPLY_FILTERS).click();
        return this;
    }

    /** Limpia todos los filtros aplicados. */
    public OpportunitiesPage clearFilters() {
        openFiltersDrawer();
        Waits.clickable(driver, CLEAR_FILTERS).click();
        return this;
    }

    public boolean showsEmptyState() {
        return Waits.isPresent(driver, EMPTY_STATE);
    }

    public String errorMessage() {
        if (!Waits.isPresent(driver, ALERT)) {
            return null;
        }
        return Waits.visible(driver, ALERT).getText();
    }

    /** Abre el detalle de la oportunidad cuyo titulo coincide. */
    public OpportunityDetailPage openDetail(String title) {
        for (WebElement card : cards()) {
            String cardTitle = card.findElement(By.tagName("h2")).getText().trim();
            if (cardTitle.equalsIgnoreCase(title.trim())) {
                card.findElement(CARD_LINK).click();
                return new OpportunityDetailPage(driver);
            }
        }
        throw new IllegalStateException(
                "No se encontro la oportunidad '" + title + "' en el listado");
    }
}
