package com.al_lado.al_lado_project.repository.usuario;

import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-04 y HU-19: Pruebas de integración de {@link UsuarioRepository}.
 *
 * <p>Valida búsquedas por email, existencia de email y conteo por estado habilitado.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryIntegrationTest {

    @Autowired
    private UsuarioRepository repository;

    @BeforeEach
    void setUp() {
        repository.save(new Trabajador("Trabajador", "trabajador@test.com", "password"));
        repository.save(new Empleador("Empleador", "empleador@test.com", "password"));
        repository.save(new Administrador());
    }

    @Test
    void findByEmail_deberiaRetornarUsuarioPorEmail() {
        Optional<Usuario> resultado = repository.findByEmail("trabajador@test.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Trabajador");
    }

    @Test
    void findByEmail_deberiaRetornarVacioSiNoExiste() {
        Optional<Usuario> resultado = repository.findByEmail("noexiste@test.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    void existsByEmail_deberiaRetornarTrueSiEmailExiste() {
        boolean existe = repository.existsByEmail("empleador@test.com");

        assertThat(existe).isTrue();
    }

    @Test
    void existsByEmail_deberiaRetornarFalseSiEmailNoExiste() {
        boolean existe = repository.existsByEmail("noexiste@test.com");

        assertThat(existe).isFalse();
    }

    @Test
    void countByHabilitado_deberiaContarUsuariosHabilitados() {
        long habilitados = repository.countByHabilitado(true);

        assertThat(habilitados).isGreaterThanOrEqualTo(3);
    }
}
