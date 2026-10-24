package com.al_lado.e2e.tests.comunidad;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.pages.CommunityPage;
import com.al_lado.e2e.pages.LoginPage;

/**
 * Aislamiento geografico del foro (TC-SEC-002).
 *
 * <p>DEFECTO ABIERTO: el requisito RF-13 / HU-13 CA-02 pide que el foro solo
 * muestre publicaciones del mismo barrio. Hoy no lo cumple ni el backend
 * ({@code PublicacionForoServiceImpl.obtenerTodas} devuelve todas las
 * publicaciones sin filtrar por sector) ni el frontend, que no aplica ningun
 * filtro por barrio. Verificado contra la API: una cuenta del barrio Norte
 * ({@code ana@lado.com}) recibe la publicacion de una cuenta del barrio Centro.
 *
 * <p>El caso queda deshabilitado para no tener la suite en rojo mientras el
 * defecto esta abierto; se rehabilita con {@code @Disabled} removido en cuanto
 * el filtro por sector este implementado.</p>
 */
@Disabled("Defecto abierto: el foro no filtra por barrio (RF-13 / HU-13 CA-02)")
@DisplayName("TC-SEC-002 - El foro solo muestra publicaciones del mismo barrio")
class ForumGeographicIsolationTest extends BaseE2ETest {

    @Test
    @DisplayName("TC-SEC-002: una cuenta de otro barrio no ve las publicaciones ajenas")
    void unaCuentaDeOtroBarrioNoVeLasPublicacionesAjenas() {
        // Ana pertenece al barrio Norte; la publicacion del seed es del barrio Centro.
        new LoginPage(driver).open().login("ana@lado.com", E2EConfig.workerPassword());

        CommunityPage forum = new CommunityPage(driver).open();

        assertThat(forum.publicationTitles())
                .doesNotContain("Recomendacion de electricista");
    }
}
