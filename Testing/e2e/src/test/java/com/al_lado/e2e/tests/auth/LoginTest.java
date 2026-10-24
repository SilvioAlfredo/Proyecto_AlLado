package com.al_lado.e2e.tests.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.LoginPage;
import com.al_lado.e2e.pages.NavBar;

/**
 * Casos de inicio de sesion (area TC-L del plan de pruebas).
 *
 * <p>Cada caso fija un unico oraculo verificable: la URL de destino y el rol
 * persistido en la sesion, o el mensaje de error ante credenciales invalidas.</p>
 */
@DisplayName("TC-L - Autenticacion")
class LoginTest extends BaseE2ETest {

    @Test
    @DisplayName("TC-L-001: el trabajador autenticado llega a su panel")
    void trabajadorValidoLlegaASuPanel() {
        LoginPage login = new LoginPage(driver).open();

        login.login(E2EConfig.workerEmail(), E2EConfig.workerPassword());

        assertThat(driver.getCurrentUrl()).contains("/trabajador/dashboard");
        assertThat(login.hasStoredSession()).isTrue();
        assertThat(login.storedRoles()).contains("ROLE_TRABAJADOR");
    }

    @Test
    @DisplayName("TC-L-002: el empleador autenticado llega a su panel")
    void empleadorValidoLlegaASuPanel() {
        LoginPage login = new LoginPage(driver).open();

        login.login(E2EConfig.employerEmail(), E2EConfig.employerPassword());

        assertThat(driver.getCurrentUrl()).contains("/empleador/dashboard");
        assertThat(login.hasStoredSession()).isTrue();
        assertThat(login.storedRoles()).contains("ROLE_EMPLEADOR");
    }

    @Test
    @DisplayName("TC-L-003: un email inexistente no abre sesion")
    void emailInexistenteMuestraError() {
        LoginPage login = new LoginPage(driver).open();

        login.login("no.existe@demo.com", "Clave123!");

        assertThat(login.hasError()).isTrue();
        assertThat(login.errorMessage()).isNotBlank();
        assertThat(login.hasStoredSession()).isFalse();
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("TC-L-004: una contrasena incorrecta no abre sesion")
    void contrasenaIncorrectaMuestraError() {
        LoginPage login = new LoginPage(driver).open();

        login.login(E2EConfig.workerEmail(), "ContrasenaEquivocada1!");

        assertThat(login.hasError()).isTrue();
        assertThat(login.hasStoredSession()).isFalse();
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("TC-L-005: el boton permite mostrar y ocultar la contrasena")
    void alternaVisibilidadDeLaContrasena() {
        LoginPage login = new LoginPage(driver).open();
        assertThat(login.passwordFieldType()).isEqualTo("password");

        login.togglePasswordVisibility();

        assertThat(login.passwordFieldType()).isEqualTo("text");
    }

    @Test
    @DisplayName("TC-L-006: la sesion se cierra y vuelve al formulario de ingreso")
    void cerrarSesionLimpiaLaSesion() {
        loginAsWorker();

        new NavBar(driver).waitUntilLoaded().logout();

        assertThat(driver.getCurrentUrl()).contains("/login");
        assertThat(new LoginPage(driver).hasStoredSession()).isFalse();
    }
}
