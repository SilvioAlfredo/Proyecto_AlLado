package com.al_lado.al_lado_project.repository.trabajo;

import com.al_lado.al_lado_project.entity.trabajo.Etiqueta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-07: Pruebas de integración de {@link EtiquetaRepository}.
 *
 * <p>Valida búsquedas de etiquetas por nombre.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
class EtiquetaRepositoryIntegrationTest {

    @Autowired
    private EtiquetaRepository repository;

    @BeforeEach
    void setUp() {
        repository.save(new Etiqueta("Pintura"));
        repository.save(new Etiqueta("Electricidad"));
    }

    @Test
    void findByNombreIgnoreCase_deberiaRetornarEtiquetaPorNombre() {
        Optional<Etiqueta> resultado = repository.findByNombreIgnoreCase("Pintura");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Pintura");
    }

    @Test
    void findByNombreIgnoreCase_deberiaRetornarVacioSiNoExiste() {
        Optional<Etiqueta> resultado = repository.findByNombreIgnoreCase("Plomeria");

        assertThat(resultado).isEmpty();
    }

    @Test
    void existsByNombreIgnoreCase_deberiaRetornarTrueSiEtiquetaExiste() {
        boolean existe = repository.existsByNombreIgnoreCase("Electricidad");

        assertThat(existe).isTrue();
    }
}
