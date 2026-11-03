package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;

import com.al_lado.al_lado_project.entity.comunidad.Publicidad;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-15: Pruebas de integración de {@link PublicidadRepository}.
 *
 * <p>Valida anuncios vigentes y por anunciante.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
class PublicidadRepositoryIntegrationTest {

    @Autowired
    private PublicidadRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Empleador anunciante;

    @BeforeEach
    void setUp() {
        anunciante = usuarioRepository.save(new Empleador("Empleador", "empleador@test.com", "password"));
    }

    @Test
    void buscarPublicidadesVigentes_deberiaRetornarAnunciosEnRangoDeFechas() {
        Publicidad vigente = new Publicidad(
                "Anuncio Vigente", "Contenido", "img.png", "destino.com",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), anunciante);
        vigente.setActiva(true);
        repository.save(vigente);

        Publicidad vencida = new Publicidad(
                "Anuncio Vencido", "Contenido", "img.png", "destino.com",
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), anunciante);
        vencida.setActiva(true);
        repository.save(vencida);

        List<Publicidad> resultado = repository.buscarPublicidadesVigentes(LocalDate.of(2026, 6, 1));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Anuncio Vigente");
    }

    @Test
    void findByAnuncianteId_deberiaRetornarAnunciosDelAnunciante() {
        Publicidad p1 = new Publicidad("Anuncio 1", "C1", "img1.png", "d1.com",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), anunciante);
        Publicidad p2 = new Publicidad("Anuncio 2", "C2", "img2.png", "d2.com",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), anunciante);
        repository.save(p1);
        repository.save(p2);

        List<Publicidad> resultado = repository.findByAnuncianteId(anunciante.getId());

        assertThat(resultado).hasSize(2);
    }
}
