package com.al_lado.e2e.tests.oportunidades;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.ApiClient;
import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.OpportunitiesPage;

/**
 * Casos del listado y los filtros de oportunidades (area TC-COB).
 *
 * <p>Las expectativas no estan escritas a mano: se derivan de la API, de modo que
 * el caso verifica que la pantalla muestra exactamente lo que el backend
 * devuelve, y no un conjunto de datos fijo que dejaria de ser valido en
 * cuanto cambia el seed.</p>
 */
@DisplayName("TC-COB - Listado y filtros de oportunidades")
class OpportunitiesTest extends BaseE2ETest {

    @Test
    @DisplayName("TC-COB-001: el listado muestra las oportunidades activas que devuelve la API")
    void elListadoCoincideConLaApi() {
        loginAsWorker();
        OpportunitiesPage opportunities = new OpportunitiesPage(driver).open().waitUntilLoaded();

        List<String> esperadas = new ApiClient().activeOpportunityTitles(token());
        List<String> visibles = opportunities.visibleOpportunityTitles();

        assertThat(esperadas).isNotEmpty();
        assertThat(visibles).containsExactlyInAnyOrderElementsOf(esperadas);
    }

    @Test
    @DisplayName("TC-COB-003: el buscador filtra por texto de la tarea")
    void elBuscadorFiltraPorTexto() {
        loginAsWorker();
        OpportunitiesPage opportunities = new OpportunitiesPage(driver).open().waitUntilLoaded();

        // Se busca por una palabra del titulo de una oportunidad existente.
        String palabra = palabraDistintiva(opportunities.visibleOpportunityTitles());
        List<String> visibles = opportunities.titlesAfterSearch(palabra);

        assertThat(visibles).isNotEmpty();
        assertThat(visibles).allMatch(titulo -> titulo.toLowerCase().contains(palabra.toLowerCase()));
    }

    @Test
    @DisplayName("TC-COB-003: un termino sin coincidencias muestra el estado vacio")
    void unaBusquedaSinCoincidenciasMuestraElEstadoVacio() {
        loginAsWorker();
        OpportunitiesPage opportunities = new OpportunitiesPage(driver).open().waitUntilLoaded();

        opportunities.search("electrodomestico inexistente");

        assertThat(opportunities.showsEmptyState()).isTrue();
        assertThat(opportunities.opportunityCount()).isZero();
    }

    @Test
    @DisplayName("TC-COB-003: el filtro de modalidad deja solo las oportunidades de esa modalidad")
    void elFiltroDeModalidadSeAplica() {
        loginAsWorker();
        OpportunitiesPage opportunities = new OpportunitiesPage(driver).open().waitUntilLoaded();
        ApiClient api = new ApiClient();
        String token = token();

        String modalidad = modalidadPresente(api, token);
        List<String> esperadas = api.opportunityTitlesByModality(token, modalidad);

        opportunities.filterByModality(modalidad);

        assertThat(opportunities.visibleOpportunityTitles())
                .containsExactlyInAnyOrderElementsOf(esperadas);
    }

    @Test
    @DisplayName("TC-COB-003: el filtro de ubicacion deja solo las oportunidades de esa zona")
    void elFiltroDeUbicacionSeAplica() {
        loginAsWorker();
        OpportunitiesPage opportunities = new OpportunitiesPage(driver).open().waitUntilLoaded();
        ApiClient api = new ApiClient();
        String token = token();

        String ubicacion = ubicacionPresente(api, token);
        List<String> esperadas = api.opportunityTitlesByLocation(token, ubicacion);

        opportunities.filterByLocation(ubicacion);

        assertThat(opportunities.visibleOpportunityTitles())
                .containsExactlyInAnyOrderElementsOf(esperadas);
    }

    @Test
    @DisplayName("TC-COB-003: el filtro de destacadas deja solo las oportunidades destacadas")
    void elFiltroDeDestacadasSeAplica() {
        loginAsWorker();
        OpportunitiesPage opportunities = new OpportunitiesPage(driver).open().waitUntilLoaded();
        ApiClient api = new ApiClient();
        String token = token();

        List<String> esperadas = api.highlightedOpportunityTitles(token);

        opportunities.filterOnlyHighlighted();

        assertThat(opportunities.visibleOpportunityTitles())
                .containsExactlyInAnyOrderElementsOf(esperadas);
    }

    /** Token del trabajador autenticado en la prueba. */
    private String token() {
        return new ApiClient().login(E2EConfig.workerEmail(), E2EConfig.workerPassword());
    }

    /** Devuelve la primera modalidad presente en los datos cargados. */
    private String modalidadPresente(ApiClient api, String token) {
        assertThat(api.activeOpportunityTitles(token)).isNotEmpty();
        return api.firstModality(token);
    }

    private String ubicacionPresente(ApiClient api, String token) {
        return api.firstLocation(token);
    }

    /** Palabra distintiva de los titulos visibles, para que el filtro no ambiguo. */
    private String palabraDistintiva(List<String> titulos) {
        for (String titulo : titulos) {
            for (String palabra : titulo.split("\\s+")) {
                if (palabra.length() >= 6) {
                    return palabra;
                }
            }
        }
        throw new AssertionError("No se encontro una palabra distintiva en los titulos: " + titulos);
    }
}
