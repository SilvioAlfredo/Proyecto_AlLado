package com.al_lado.e2e.tests.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.pages.RegisterPage;

/**
 * Casos de alta de cuentas y de validacion de contrasena (area TC-LIM).
 *
 * <p>El campo de contrasena declara {@code minlength="8"}, de modo que el navegador
 * bloquea el envio antes de llegar al backend: el caso de 7 caracteres se verifica
 * por la ausencia del banner de exito y la permanencia en el formulario.</p>
 */
@DisplayName("TC-LIM - Alta de cuentas y limites de contrasena")
class RegisterTest extends BaseE2ETest {

    @Test
    @DisplayName("TC-LIM-003a: una contrasena de 7 caracteres no habilita el alta")
    void contrasenaDeSieteCaracteresEsRechazada() {
        RegisterPage register = new RegisterPage(driver).open();
        register.selectWorkerRole();
        register.fillPersonalData("Nico", "Prueba", uniqueEmail(), "+549110000123");
        register.fillPassword("Clave12");

        register.submit();

        assertThat(register.waitForOutcome()).isFalse();
        assertThat(register.successMessage()).isNull();
        assertThat(driver.getCurrentUrl()).contains("/register");
        assertThat(register.isPasswordLongEnough("Clave12")).isFalse();
        assertThat(register.passwordHelp()).contains("8");
    }

    @Test
    @DisplayName("TC-LIM-003b: una contrasena de 8 caracteres habilita el alta")
    void contrasenaDeOchoCaracteresEsAceptada() {
        RegisterPage register = new RegisterPage(driver).open();
        register.selectWorkerRole();
        register.fillPersonalData("Nico", "Prueba", uniqueEmail(), "+549110000123");
        register.fillPassword("Clave123");

        register.submit();

        assertThat(register.waitForOutcome()).isTrue();
        assertThat(register.successMessage()).contains("Cuenta creada");
        assertThat(register.submitButtonText()).contains("Cuenta creada");
    }

    @Test
    @DisplayName("TC-L-004: un email ya registrado devuelve el error de duplicado")
    void emailDuplicadoDevuelveError() {
        RegisterPage register = new RegisterPage(driver).open();
        register.selectWorkerRole();
        register.fillPersonalData("Repetida", "Cuenta", "trabajador@demo.com", "+549110000124");
        register.fillPassword("Clave123!");

        register.submit();
        register.waitForOutcome();

        assertThat(register.errorMessage()).isNotBlank();
        assertThat(register.successMessage()).isNull();
    }

    @Test
    @DisplayName("TC-LIM-005: el selector de rol distingue trabajador de empleador")
    void elSelectorDeRolQuedaMarcado() {
        RegisterPage register = new RegisterPage(driver).open();

        register.selectEmployerRole();
        assertThat(register.isRoleSelected("EMPLEADOR")).isTrue();
        assertThat(register.isRoleSelected("TRABAJADOR")).isFalse();

        register.selectWorkerRole();
        assertThat(register.isRoleSelected("TRABAJADOR")).isTrue();
        assertThat(register.isRoleSelected("EMPLEADOR")).isFalse();
    }

    private String uniqueEmail() {
        return "qa." + UUID.randomUUID().toString().substring(0, 8) + "@demo.com";
    }
}
