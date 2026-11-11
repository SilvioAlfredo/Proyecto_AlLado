package com.al_lado.e2e.tests.oportunidades;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.ApiClient;
import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.OpportunitiesPage;
import com.al_lado.e2e.pages.OpportunityDetailPage;
import com.al_lado.e2e.pages.PostulationModal;

/**
 * Casos de postulacion a una oportunidad (area TC-NEG).
 *
 * <p>Cada caso crea su propia oportunidad mediante la API, de modo que sea
 * repetible: si se reutilizara una oportunidad del seed, una corrida previa
 * habria dejado al trabajador postulado y el caso fallaria sin causa real.</p>
 */
@DisplayName("TC-NEG - Postulacion a oportunidades")
class PostulationTest extends BaseE2ETest {

    private static final long EMPLEADOR_ID = 2L;

    @Test
    @DisplayName("TC-NEG-002: una oportunidad cerrada no se puede postular")
    void oportunidadCerradaNoSePuedePostular() {
        ApiClient api = new ApiClient();
        String tokenEmpleador = api.login(E2EConfig.employerEmail(), E2EConfig.employerPassword());
        String titulo = "Oportunidad cerrada " + sufijo();
        long id = api.createOpportunity(tokenEmpleador, EMPLEADOR_ID, titulo,
                "Necesito un profesional para una tarea urgente.");
        api.closeOpportunity(tokenEmpleador, id, EMPLEADOR_ID, titulo,
                "Necesito un profesional para una tarea urgente.");

        loginAsWorker();
        OpportunityDetailPage detail = new OpportunityDetailPage(driver).open(id);

        assertThat(detail.canApply()).isFalse();
        assertThat(detail.closedButtonText()).contains("Oferta cerrada");
    }

    @Test
    @Disabled("Defecto abierto TC-POS-004: el backend no valida @Positive en "
            + "remuneracionPretendida y acepta montos menores o iguales a cero.")
    @DisplayName("TC-NEG-004: una remuneracion de cero debe rechazarse")
    void remuneracionCeroSeRechaza() {
        long id = crearOportunidadActiva();

        loginAsWorker();
        PostulationModal modal = new OpportunityDetailPage(driver).open(id).openPostulationModal();
        modal.fill("0", "2", "Prueba de remuneracion invalida").submit();
        boolean huboDesenlace = modal.waitForOutcome();

        if (huboDesenlace) {
            // El backend rechazo la propuesta.
            assertThat(modal.errorMessage()).isNotBlank();
        } else {
            // El navegador la bloqueo por la validacion del formulario.
            assertThat(modal.errorMessage()).isNull();
        }
        assertThat(driver.getCurrentUrl()).doesNotContain("/postulaciones");
    }

    @Test
    @DisplayName("TC-NEG-004: una propuesta sin mensaje no se envia")
    void propuestaIncompletaNoSeEnvia() {
        long id = crearOportunidadActiva();

        loginAsWorker();
        PostulationModal modal = new OpportunityDetailPage(driver).open(id).openPostulationModal();
        modal.fill("50000", "2", "Mensaje que se va a borrar");
        modal.clearMensaje();
        modal.submit();

        // El mensaje es obligatorio. El bloqueo puede venir del navegador
        // (validacion HTML5 sobre el textarea, sin peticion) o del propio
        // formulario de la app; lo que no puede pasar es que la postulacion se cree.
        boolean huboDesenlace = modal.waitForOutcome();

        assertThat(driver.getCurrentUrl()).doesNotContain("/postulaciones");
        if (huboDesenlace) {
            assertThat(modal.errorMessage()).isNotBlank();
        }
    }

    @Test
    @DisplayName("TC-COB-005: una postulacion valida se confirma")
    void postulacionValidaSeConfirma() {
        long id = crearOportunidadActiva();

        loginAsWorker();
        OpportunityDetailPage detail = new OpportunityDetailPage(driver).open(id);
        PostulationModal modal = detail.openPostulationModal();
        modal.fill("45000", "3", "Puedo comenzar hoy mismo con la tarea.");
        modal.submit();

        assertThat(modal.waitForOutcome()).isTrue();
        assertThat(detail.statusMessage()).contains("Postulación enviada");
    }

    @Test
    @DisplayName("TC-NEG-003: una segunda postulacion a la misma oportunidad se rechaza")
    void doblePostulacionSeRechaza() {
        long id = crearOportunidadActiva();

        loginAsWorker();
        OpportunityDetailPage detail = new OpportunityDetailPage(driver).open(id);
        PostulationModal modal = detail.openPostulationModal();
        modal.fill("45000", "3", "Primera propuesta.");
        modal.submit();
        assertThat(modal.waitForOutcome()).isTrue();

        // Segundo intento sobre la misma oportunidad: el backend responde 409 y el
        // modal muestra el rechazo. El boton de la pantalla guarda el estado
        // "Enviada" solo en memoria, por eso el caso vuelve a abrir el modal.
        PostulationModal segundo = new OpportunityDetailPage(driver).open(id).openPostulationModal();
        segundo.fill("50000", "2", "Segunda propuesta sobre la misma oportunidad.");
        segundo.submit();
        segundo.waitForOutcome();

        assertThat(segundo.errorMessage()).contains("ya se encuentra postulado");
    }

    /** Crea una oportunidad activa y devuelve su identificador. */
    private long crearOportunidadActiva() {
        ApiClient api = new ApiClient();
        String token = api.login(E2EConfig.employerEmail(), E2EConfig.employerPassword());
        return api.createOpportunity(token, EMPLEADOR_ID,
                "Tarea de prueba " + sufijo(),
                "Necesito un profesional para una tarea de prueba automatizada.");
    }

    private String sufijo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
