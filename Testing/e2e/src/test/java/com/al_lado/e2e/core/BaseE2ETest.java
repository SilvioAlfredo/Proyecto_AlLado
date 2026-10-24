package com.al_lado.e2e.core;

import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import com.al_lado.e2e.pages.LoginPage;

/**
 * Clase base de los casos E2E.
 *
 * <p>El navegador lo crea y cierra {@link EvidenceExtension}, que ademas deja la
 * evidencia del caso en {@code target/e2e-evidencias}. Esta clase aporta los
 * atajos de autenticacion usados por los casos.</p>
 */
@ExtendWith(EvidenceExtension.class)
public abstract class BaseE2ETest {

    protected WebDriver driver;

    /** Enyecta el navegador activo (lo llama la extension). */
    void attachDriver(WebDriver driver) {
        this.driver = driver;
    }

    /** Suelta la referencia al navegador entre casos (lo llama la extension). */
    void detachDriver() {
        this.driver = null;
    }

    /** Navegador activo del caso. */
    WebDriver driver() {
        return driver;
    }

    /** Inicia sesion como trabajador y espera al panel del rol. */
    protected void loginAsWorker() {
        loginAsWorker(E2EConfig.workerEmail(), E2EConfig.workerPassword());
    }

    protected void loginAsWorker(String email, String password) {
        new LoginPage(driver).open().login(email, password);
        Waits.urlContains(driver, "/trabajador/dashboard");
    }

    /** Inicia sesion como empleador y espera al panel del rol. */
    protected void loginAsEmployer() {
        loginAsEmployer(E2EConfig.employerEmail(), E2EConfig.employerPassword());
    }

    protected void loginAsEmployer(String email, String password) {
        new LoginPage(driver).open().login(email, password);
        Waits.urlContains(driver, "/empleador/dashboard");
    }

    /** Inicia sesion como administrador desde el acceso restringido. */
    protected void loginAsAdmin() {
        loginAsAdmin(E2EConfig.adminEmail(), E2EConfig.adminPassword());
    }

    protected void loginAsAdmin(String email, String password) {
        new LoginPage(driver).openAdminAccess().login(email, password);
        Waits.urlContains(driver, "/admin/dashboard");
    }
}
