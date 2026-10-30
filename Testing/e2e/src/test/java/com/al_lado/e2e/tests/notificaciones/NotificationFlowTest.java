package com.al_lado.e2e.tests.notificaciones;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.al_lado.e2e.core.ApiClient;
import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.core.Waits;
import com.al_lado.e2e.pages.LoginPage;
import com.al_lado.e2e.pages.NavBar;
import com.al_lado.e2e.pages.OpportunityDetailPage;
import com.al_lado.e2e.pages.PostulationModal;

@DisplayName("TC-NOTIF - Flujo de notificaciones")
class NotificationFlowTest extends BaseE2ETest {

    private static final long EMPLEADOR_ID = 2L;
    private static final By TITULO_NOTIFICACION = By.xpath(
            "//p[normalize-space()='Nueva postulación recibida']");
    private static final By MARCAR_TODAS = By.xpath(
            "//button[normalize-space()='Marcar todas como leídas']");

    @Test
    @DisplayName("TC-NOTIF-003: una postulación notifica al empleador y puede marcarse leída")
    void unaPostulacionNotificaAlEmpleadorYPuedeMarcarseLeida() {
        ApiClient api = new ApiClient();
        String titulo = "Notificación E2E " + UUID.randomUUID().toString().substring(0, 8);
        long oportunidadId = api.createOpportunity(
                api.login(E2EConfig.employerEmail(), E2EConfig.employerPassword()),
                EMPLEADOR_ID,
                titulo,
                "Oportunidad creada para validar notificaciones.");

        loginAsWorker();
        OpportunityDetailPage detalle = new OpportunityDetailPage(driver).open(oportunidadId);
        PostulationModal postulacion = detalle.openPostulationModal();
        postulacion.fill("45000", "3", "Tengo experiencia para completar esta tarea.").submit();

        assertThat(postulacion.waitForOutcome()).isTrue();
        assertThat(postulacion.statusMessage()).contains("Postulación enviada");

        new NavBar(driver).logout();
        new LoginPage(driver).open().login(E2EConfig.employerEmail(), E2EConfig.employerPassword());
        Waits.urlContains(driver, "/empleador/dashboard");
        driver.get(E2EConfig.baseUrl() + "/empleador/notificaciones");

        Waits.visible(driver, TITULO_NOTIFICACION);
        Waits.clickable(driver, MARCAR_TODAS).click();
        Waits.absent(driver, MARCAR_TODAS);

        assertThat(driver.findElements(TITULO_NOTIFICACION)).hasSize(1);
    }
}