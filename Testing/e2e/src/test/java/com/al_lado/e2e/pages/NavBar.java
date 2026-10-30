package com.al_lado.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.al_lado.e2e.core.Waits;

/**
 * Barra de navegacion global, presente en todas las paginas.
 *
 * <p>Es la unica que muestra el boton de salida en todas las vistas (incluido el
 * panel de administracion), por eso se usa como referencia para el estado de
 * sesion.</p>
 */
public class NavBar extends BasePage {

    private static final By NAV = By.cssSelector("nav[aria-label='Navegación principal']");
    private static final By LOGOUT = By.xpath(
            "//nav[@aria-label='Navegación principal']//button[normalize-space()='Salir']");
    private static final By LOGIN_LINK = By.xpath(
            "//nav[@aria-label='Navegación principal']//a[normalize-space()='Ingresar']");

    public NavBar(WebDriver driver) {
        super(driver);
    }

    /** Espera a que la barra de navegacion este disponible. */
    public NavBar waitUntilLoaded() {
        Waits.visible(driver, NAV);
        return this;
    }

    /** Indica si la sesion esta iniciada (el boton de salida solo existe con sesion). */
    public boolean isLoggedIn() {
        return Waits.isPresent(driver, LOGOUT);
    }

    public boolean hasLoginLink() {
        return Waits.isPresent(driver, LOGIN_LINK);
    }

    /** Pulsa "Salir" y espera a volver al formulario de ingreso. */
    public void logout() {
        Waits.clickable(driver, LOGOUT).click();
        Waits.urlContains(driver, "/login");
    }
}
