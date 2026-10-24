package com.al_lado.e2e.core;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Esperas explicitas para la suite E2E.
 *
 * <p>Nunca se usan esperas fijas ({@code Thread.sleep}): la app es una SPA de
 * Next.js con renderizado en cliente, por lo que el HTML puede llegar antes que
 * los datos.</p>
 */
public final class Waits {

    private Waits() {
    }

    public static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, E2EConfig.timeout());
    }

    /** Espera con un limite propio, para casos donde la ausencia de un evento es el oraculo. */
    public static boolean waitUntil(WebDriver driver, Duration timeout,
            java.util.function.Function<WebDriver, Boolean> condition) {
        try {
            return Boolean.TRUE.equals(new WebDriverWait(driver, timeout).until(condition));
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public static WebElement visible(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static List<WebElement> visibleAll(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static WebElement clickable(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean isVisible(WebDriver driver, By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public static boolean isPresent(WebDriver driver, By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /** Espera a que la URL contenga el fragmento esperado (por ejemplo el panel de destino). */
    public static boolean urlContains(WebDriver driver, String fragment) {
        return wait(driver).until(ExpectedConditions.urlContains(fragment));
    }

    /** Espera a que un texto cualquiera del cuerpo de la pagina contenga el fragmento. */
    public static boolean bodyContains(WebDriver driver, String fragment) {
        return wait(driver).until(driver1 -> driver1.getPageSource().contains(fragment));
    }

    /** Espera a que desaparezca un elemento (modales de error, spinners). */
    public static void absent(WebDriver driver, By locator) {
        wait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /** Espera de cortesia para estabilizar animaciones antes de capturar evidencia. */
    public static void settle() {
        try {
            Thread.sleep(Duration.ofMillis(300));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
