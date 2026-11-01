package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-20: Pruebas de integración de {@link ComunicadoRepository}.
 *
 * <p>Valida listado paginado y filtrado por alcance.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ComunicadoRepositoryIntegrationTest {

    @Autowired
    private ComunicadoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Administrador admin;

    @BeforeEach
    void setUp() {
        admin = usuarioRepository.save(new Administrador());
    }

    @Test
    void findAllByOrderByFechaEmisionDesc_deberiaRetornarComunicadosPaginados() {
        Comunicado c1 = new Comunicado("Titulo 1", "Contenido 1", "GLOBAL", admin);
        Comunicado c2 = new Comunicado("Titulo 2", "Contenido 2", "LOCAL", admin);
        repository.save(c1);
        repository.save(c2);

        List<Comunicado> resultado = repository.findAllByOrderByFechaEmisionDesc(PageRequest.of(0, 10)).getContent();

        assertThat(resultado).hasSize(2);
    }

    @Test
    void findByAlcanceIgnoreCase_deberiaFiltrarPorAlcance() {
        Comunicado c1 = new Comunicado("Global 1", "C1", "GLOBAL", admin);
        Comunicado c2 = new Comunicado("Local 1", "C2", "LOCAL", admin);
        repository.save(c1);
        repository.save(c2);

        List<Comunicado> resultado = repository.findByAlcanceIgnoreCase("GLOBAL", PageRequest.of(0, 10)).getContent();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getAlcance()).isEqualTo("GLOBAL");
    }
}
