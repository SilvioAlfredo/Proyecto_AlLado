package com.al_lado.e2e.core;

import java.io.File;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Construye el WebDriver segun la configuracion del entorno.
 *
 * <p>Selenium Manager (incluido en Selenium 4.6+) descarga y cachea el driver
 * correspondiente al navegador, por lo que no hace falta distribuir
 * geckodriver/chromedriver dentro del repositorio.</p>
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver create() {
        String browser = E2EConfig.browser();
        WebDriver driver = switch (browser) {
            case "firefox" -> createFirefox();
            case "chrome" -> createChrome();
            default -> throw new IllegalArgumentException(
                    "Navegador no soportado: " + browser + " (usar firefox o chrome)");
        };
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        return driver;
    }

    private static WebDriver createFirefox() {
        FirefoxOptions options = new FirefoxOptions();
        if (E2EConfig.headless()) {
            options.addArguments("-headless");
        }
        options.addArguments("-width=1440", "-height=900");
        options.addPreference("intl.accept_languages", "es-ES,es");
        if (!E2EConfig.firefoxBinary().isBlank()) {
            options.setBinary(E2EConfig.firefoxBinary());
        }
        return new FirefoxDriver(options);
    }

    private static WebDriver createChrome() {
        ChromeOptions options = new ChromeOptions();
        if (E2EConfig.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,900", "--no-sandbox", "--disable-dev-shm-usage");
        options.addArguments("--lang=es-ES");
        if (!E2EConfig.chromeBinary().isBlank()) {
            options.setBinary(E2EConfig.chromeBinary());
        }
        if (E2EConfig.chromeDriverPath().isBlank()) {
            return new ChromeDriver(options);
        }
        // Driver explicito: evita que Selenium Manager intente resolverlo solo.
        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(E2EConfig.chromeDriverPath()))
                .build();
        return new ChromeDriver(service, options);
    }
}
