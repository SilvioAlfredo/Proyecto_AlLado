package com.al_lado.e2e.tests.comunidad;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.ApiClient;
import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.core.Waits;
import com.al_lado.e2e.pages.CommunityPage;

/**
 * Casos del foro de la comunidad (area TC-M).
 */
@DisplayName("TC-M - Foro de la comunidad")
class CommunityTest extends BaseE2ETest {

    @Test
    @DisplayName("TC-M-001: el foro lista las publicaciones que devuelve la API")
    void elForoListaLasPublicaciones() {
        loginAsWorker();

        CommunityPage forum = new CommunityPage(driver).open();
        String token = new ApiClient().login(E2EConfig.workerEmail(), E2EConfig.workerPassword());

        assertThat(forum.heading()).isEqualTo("Comunidad");
        assertThat(forum.publicationCount()).isGreaterThan(0);
        assertThat(forum.publicationTitles())
                .containsExactlyInAnyOrderElementsOf(new ApiClient().forumTitles(token));
    }

    @Test
    @DisplayName("TC-M-002: el trabajador publica en el foro")
    void elTrabajadorPublicaEnElForo() {
        loginAsWorker();
        CommunityPage forum = new CommunityPage(driver).open();
        String titulo = "Publicacion de prueba QA " + System.nanoTime();

        forum.createPublication(titulo, "Contenido de la publicacion de prueba.");

        assertThat(forum.statusMessage()).contains("Publicación creada");
        assertThat(forum.publicationTitles()).contains(titulo);
    }

    @Test
    @DisplayName("TC-M-003: sin sesion, el foro no se puede consultar")
    void sinSesionElForoNoSeConsulta() {
        driver.get(E2EConfig.baseUrl() + "/trabajador/comunidad");

        Waits.urlContains(driver, "/login");
        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
