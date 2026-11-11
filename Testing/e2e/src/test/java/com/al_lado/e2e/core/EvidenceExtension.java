package com.al_lado.e2e.core;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

/**
 * Extension que administra el ciclo de vida del navegador y guarda la evidencia
 * de cada caso.
 *
 * <p><b>Una instancia de Chrome por clase de prueba.</b> El navegador se abre una
 * sola vez en {@link #beforeAll} y se reutiliza para todos los casos de la clase;
 * entre casos se borran las cookies y el almacenamiento del navegador para que
 * cada uno arranque en un estado limpio. Al terminar la clase se cierra. Esto
 * evita el costo de lanzar el navegador en cada caso (que era el cuello de
 * botella de la suite) sin que los casos se contaminen entre si.</p>
 *
 * <p><b>Evidencia siempre.</b> Cada caso deja una captura de pantalla, el HTML de
 * la pagina y la URL final. La captura se toma dentro de {@link #afterEach},
 * antes de cerrar el navegador: ahi el caso ya termino y el contexto de extension
 * ya conoce la excepcion del metodo de prueba.</p>
 *
 * <p>Con {@code E2E_CAPTURE_SUCCESS=false} se puede limitar la evidencia a los
 * casos que fallan.</p>
 */
public class EvidenceExtension implements BeforeAllCallback, BeforeEachCallback,
        AfterEachCallback, AfterAllCallback {

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(EvidenceExtension.class);

    /**
     * Navegador de la clase en curso. JUnit crea una instancia de la extension
     * por cada clase de prueba, asi que aqui vive un solo Chrome a la vez.
     */
    private WebDriver driver;

    @Override
    public void beforeAll(ExtensionContext context) {
        driver = DriverFactory.create();
        // Red de seguridad: si la corrida se interrumpe, el navegador se cierra
        // igual y no quedan procesos de Chrome huerfanos.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (driver != null) {
                driver.quit();
            }
        }));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        if (driver == null) {
            return;
        }
        // Cada caso arranca desde la home y sin sesion heredada.
        driver.get(E2EConfig.baseUrl());
        limpiarSesion(driver);
        testInstance(context).ifPresent(test -> test.attachDriver(driver));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        try {
            if (debeCapturar(context)) {
                EvidenceCapture.capture(driver, identifier(context), context.getDisplayName());
            }
        } finally {
            // El navegador se cierra al terminar la clase, no en cada caso.
            testInstance(context).ifPresent(BaseE2ETest::detachDriver);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    /** Borra cookies y almacenamiento local para partir de una sesion limpia. */
    private void limpiarSesion(WebDriver driver) {
        try {
            driver.manage().deleteAllCookies();
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "window.localStorage.clear(); window.sessionStorage.clear();");
        } catch (RuntimeException ignored) {
            // Si la pagina todavia no admite scripting se sigue adelante: el caso
            // verificara de todos modos el estado real de la sesion.
        }
    }

    /**
     * La evidencia se guarda siempre. Con {@code E2E_CAPTURE_ONLY_FAILURES=true}
     * se limita a los casos que fallan, para las corridas rapidas de diagnostico.
     */
    private boolean debeCapturar(ExtensionContext context) {
        boolean soloFallos = Boolean.parseBoolean(
                System.getenv().getOrDefault("E2E_CAPTURE_ONLY_FAILURES", "false"));
        return !soloFallos || context.getExecutionException().isPresent();
    }

    private java.util.Optional<BaseE2ETest> testInstance(ExtensionContext context) {
        return context.getTestInstance()
                .filter(BaseE2ETest.class::isInstance)
                .map(BaseE2ETest.class::cast);
    }

    /** Identificador del caso, usado como nombre de la carpeta de evidencia. */
    private String identifier(ExtensionContext context) {
        return context.getTestClass().map(Class::getSimpleName).orElse("Caso")
                + "." + context.getTestMethod().map(m -> m.getName()).orElse("desconocido");
    }
}
