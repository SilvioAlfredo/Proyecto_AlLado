package com.al_lado.al_lado_project.repository.perfil;

import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;

import com.al_lado.al_lado_project.entity.perfil.PortafolioItem;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-17: Pruebas de integración de {@link PortafolioItemRepository}.
 *
 * <p>Valida listado de ítems de portafolio por trabajador.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
class PortafolioItemRepositoryIntegrationTest {

    @Autowired
    private PortafolioItemRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Trabajador trabajador;

    @BeforeEach
    void setUp() {
        trabajador = usuarioRepository.save(new Trabajador("Trabajador", "trabajador@test.com", "password"));
    }

    @Test
    void findByTrabajadorId_deberiaRetornarItemsDelTrabajador() {
        PortafolioItem i1 = new PortafolioItem("Proyecto 1", "Descripcion 1", "url1.pdf", trabajador);
        PortafolioItem i2 = new PortafolioItem("Proyecto 2", "Descripcion 2", "url2.pdf", trabajador);
        repository.save(i1);
        repository.save(i2);

        List<PortafolioItem> resultado = repository.findByTrabajadorId(trabajador.getId());

        assertThat(resultado).hasSize(2);
    }
}
