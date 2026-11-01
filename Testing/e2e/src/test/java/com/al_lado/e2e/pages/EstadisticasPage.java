package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.al_lado.e2e.core.Waits;

/**
 * Página de estadísticas del trabajador ({@code /trabajador/perfil/estadisticas}).
 *
 * <p>Permite al trabajador ver sus estadísticas de ingresos estimados y
 * trabajos realizados (HU-12).</p>
 */
public class EstadisticasPage extends BasePage {

    public static final String PATH = "/trabajador/perfil/estadisticas";

    private static final By TITLE = By.cssSelector("h1");
    private static final By INGRESOS_TOTALES = By.cssSelector("[data-testid='estadistica-ingresos-totales']");
    private static final By INGRESOS_ULTIMOS_3_MESES = By.cssSelector("[data-testid='estadistica-ingresos-3-meses']");
    private static final By POSTULACIONES_ENVIADAS = By.cssSelector("[data-testid='estadistica-postulaciones-enviadas']");
    private static final By POSTULACIONES_COMPLETADAS = By.cssSelector("[data-testid='estadistica-postulaciones-completadas']");
    private static final By VALORACIONES_PROMEDIO = By.cssSelector("[data-testid='estadistica-valoraciones-promedio']");
    private static final By SUSCRIPCION_ACTIVA = By.cssSelector("[data-testid='estadistica-suscripcion-activa']");

    public EstadisticasPage(WebDriver driver) {
        super(driver);
    }

    public EstadisticasPage open() {
        navigateTo(PATH);
        Waits.visible(driver, TITLE);
        return this;
    }

    public String ingresosTotales() {
        return Waits.visible(driver, INGRESOS_TOTALES).getText();
    }

    public String ingresosUltimos3Meses() {
        return Waits.visible(driver, INGRESOS_ULTIMOS_3_MESES).getText();
    }

    public String postulacionesEnviadas() {
        return Waits.visible(driver, POSTULACIONES_ENVIADAS).getText();
    }

    public String postulacionesCompletadas() {
        return Waits.visible(driver, POSTULACIONES_COMPLETADAS).getText();
    }

    public String valoracionesPromedio() {
        return Waits.visible(driver, VALORACIONES_PROMEDIO).getText();
    }

    public boolean tieneSuscripcionActiva() {
        return Waits.isPresent(driver, SUSCRIPCION_ACTIVA);
    }
}
