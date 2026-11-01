package com.al_lado.al_lado_project.repository.usuario;

import com.al_lado.al_lado_project.entity.usuario.Administrador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-18: Pruebas de integración de {@link AdministradorRepository}.
 *
 * <p>Valida operaciones CRUD básicas de administradores.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdministradorRepositoryIntegrationTest {

    @Autowired
    private AdministradorRepository repository;

    @BeforeEach
    void setUp() {
        repository.save(new Administrador());
    }

    @Test
    void findById_deberiaRetornarAdministradorExistente() {
        Administrador admin = repository.save(new Administrador());
        admin.setNombre("Admin Test");
        admin.setEmail("admin@test.com");

        Optional<Administrador> resultado = repository.findById(admin.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    void existsById_deberiaRetornarTrueSiExiste() {
        Administrador admin = repository.save(new Administrador());

        boolean existe = repository.existsById(admin.getId());

        assertThat(existe).isTrue();
    }

    @Test
    void findAll_deberiaRetornarTodosLosAdministradores() {
        repository.save(new Administrador());

        Iterable<Administrador> resultado = repository.findAll();

        assertThat(resultado).hasSizeGreaterThanOrEqualTo(2);
    }
}
