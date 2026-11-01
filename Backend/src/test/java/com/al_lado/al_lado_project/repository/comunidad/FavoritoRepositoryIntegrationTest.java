package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Favorito;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-11: Pruebas de integración de {@link FavoritoRepository}.
 *
 * <p>Valida listado de favoritos, existencia de duplicado y eliminación por par empleador-trabajador.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FavoritoRepositoryIntegrationTest {

    @Autowired
    private FavoritoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Empleador empleador;
    private Trabajador trabajador;

    @BeforeEach
    void setUp() {
        empleador = usuarioRepository.save(new Empleador("Empleador", "empleador@test.com", "password"));
        trabajador = usuarioRepository.save(new Trabajador("Trabajador", "trabajador@test.com", "password"));
    }

    @Test
    void findByEmpleadorId_deberiaRetornarFavoritosDelEmpleador() {
        Favorito f1 = new Favorito(empleador, trabajador);
        repository.save(f1);

        List<Favorito> resultado = repository.findByEmpleadorId(empleador.getId());

        assertThat(resultado).hasSize(1);
    }

    @Test
    void existsByEmpleadorIdAndTrabajadorId_deberiaDetectarFavoritoExistente() {
        Favorito f = new Favorito(empleador, trabajador);
        repository.save(f);

        boolean existe = repository.existsByEmpleadorIdAndTrabajadorId(empleador.getId(), trabajador.getId());

        assertThat(existe).isTrue();
    }

    @Test
    void existsByEmpleadorIdAndTrabajadorId_deberiaRetornarFalseSiNoExiste() {
        boolean existe = repository.existsByEmpleadorIdAndTrabajadorId(empleador.getId(), trabajador.getId());

        assertThat(existe).isFalse();
    }

    @Test
    void deleteByEmpleadorIdAndTrabajadorId_deberiaEliminarFavorito() {
        Favorito f = new Favorito(empleador, trabajador);
        repository.save(f);

        repository.deleteByEmpleadorIdAndTrabajadorId(empleador.getId(), trabajador.getId());

        boolean existe = repository.existsByEmpleadorIdAndTrabajadorId(empleador.getId(), trabajador.getId());
        assertThat(existe).isFalse();
    }
}
