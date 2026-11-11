package com.al_lado.e2e.core;

import java.time.Duration;

/**
 * Configuracion de la suite E2E leida desde el entorno.
 *
 * <p>Regla de precedencia: variable de entorno, luego propiedad del sistema y por
 * ultimo el valor por defecto. Permite correr la misma suite contra los puertos
 * estandar (3000/8080) o contra puertos alternativos sin tocar codigo.</p>
 *
 * <table>
 *   <caption>Variables soportadas</caption>
 *   <tr><th>Variable</th><th>Defecto</th><th>Uso</th></tr>
 *   <tr><td>E2E_BASE_URL</td><td>http://localhost:3000</td><td>Raiz del frontend Next.js</td></tr>
 *   <tr><td>E2E_API_URL</td><td>http://localhost:8080</td><td>Backend Spring Boot</td></tr>
 *   <tr><td>E2E_BROWSER</td><td>firefox</td><td>firefox | chrome</td></tr>
 *   <tr><td>E2E_HEADLESS</td><td>true</td><td>false abre el navegador visible</td></tr>
 *   <tr><td>E2E_TIMEOUT</td><td>15</td><td>Espera explicita maxima, en segundos</td></tr>
 *   <tr><td>E2E_WORKER_EMAIL</td><td>trabajador@demo.com</td><td>Cuenta rol TRABAJADOR</td></tr>
 *   <tr><td>E2E_WORKER_PASSWORD</td><td>Clave123!</td><td>Contrasena del trabajador</td></tr>
 *   <tr><td>E2E_EMPLOYER_EMAIL</td><td>empleador@demo.com</td><td>Cuenta rol EMPLEADOR</td></tr>
 *   <tr><td>E2E_EMPLOYER_PASSWORD</td><td>Clave123!</td><td>Contrasena del empleador</td></tr>
 *   <tr><td>E2E_ADMIN_EMAIL</td><td>admin@allado.com</td><td>Cuenta rol ADMIN</td></tr>
 *   <tr><td>E2E_ADMIN_PASSWORD</td><td>Clave123!</td><td>Contrasena del administrador</td></tr>
 *   <tr><td>E2E_EVIDENCE_DIR</td><td>target/e2e-evidencias</td><td>Destino de capturas</td></tr>
 * </table>
 */
public final class E2EConfig {

    private E2EConfig() {
    }

    public static String baseUrl() {
        return trimTrailingSlash(value("E2E_BASE_URL", "e2e.base.url", "http://localhost:3000"));
    }

    public static String apiUrl() {
        return trimTrailingSlash(value("E2E_API_URL", "e2e.api.url", "http://localhost:8080"));
    }

    /** Navegador a usar: {@code firefox} o {@code chrome}. */
    public static String browser() {
        return value("E2E_BROWSER", "e2e.browser", "chrome").toLowerCase();
    }

    /**
     * Ruta del ejecutable de Chrome, cuando no esta en el PATH.
     * Necesario en instalaciones portables (por ejemplo Chrome for Testing).
     */
    public static String chromeBinary() {
        return value("E2E_CHROME_BINARY", "e2e.chrome.binary", "");
    }

    /** Ruta del chromedriver, cuando no esta en el PATH. */
    public static String chromeDriverPath() {
        return value("E2E_CHROMEDRIVER", "e2e.chromedriver", "");
    }

    /** Ruta del ejecutable de Firefox, cuando no esta en el PATH. */
    public static String firefoxBinary() {
        return value("E2E_FIREFOX_BINARY", "e2e.firefox.binary", "");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(value("E2E_HEADLESS", "e2e.headless", "true"));
    }

    public static Duration timeout() {
        return Duration.ofSeconds(Long.parseLong(value("E2E_TIMEOUT", "e2e.timeout", "15")));
    }

    public static String workerEmail() {
        return value("E2E_WORKER_EMAIL", "e2e.worker.email", "trabajador@demo.com");
    }

    public static String workerPassword() {
        return value("E2E_WORKER_PASSWORD", "e2e.worker.password", "Clave123!");
    }

    public static String employerEmail() {
        return value("E2E_EMPLOYER_EMAIL", "e2e.employer.email", "empleador@demo.com");
    }

    public static String employerPassword() {
        return value("E2E_EMPLOYER_PASSWORD", "e2e.employer.password", "Clave123!");
    }

    public static String adminEmail() {
        return value("E2E_ADMIN_EMAIL", "e2e.admin.email", "admin@allado.com");
    }

    public static String adminPassword() {
        return value("E2E_ADMIN_PASSWORD", "e2e.admin.password", "Clave123!");
    }

    /** Directorio donde se guardan las capturas de pantalla y el HTML de pagina. */
    public static String evidenceDir() {
        return value("E2E_EVIDENCE_DIR", "e2e.evidence.dir", "target/e2e-evidencias");
    }

    private static String value(String envKey, String systemProperty, String defaultValue) {
        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        String fromProperty = System.getProperty(systemProperty);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty.trim();
        }
        return defaultValue;
    }

    private static String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
