package com.al_lado.al_lado_project.repository.usuario;

import com.al_lado.al_lado_project.entity.usuario.Administrador;
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
 * HU-18: Pruebas de integración de {@link AdministradorRepository}.
 *
 * <p>Valida operaciones CRUD básicas de administradores.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
class AdministradorRepositoryIntegrationTest {

    @Autowired
    private AdministradorRepository repository;

    @BeforeEach
    void setUp() {
        // Cada test crea su propio administrador; @DirtiesContext limpia la BD antes de cada método
    }

    @Test
    void findById_deberiaRetornarAdministradorExistente() {
        Administrador admin = repository.save(new Administrador("Admin", "admin@test.com", "password", "SUPER"));
        admin.setNombre("Admin Test");
        admin.setEmail("admin@test.com");

        Optional<Administrador> resultado = repository.findById(admin.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    void existsById_deberiaRetornarTrueSiExiste() {
        Administrador admin = repository.save(new Administrador("Admin", "admin@test.com", "password", "SUPER"));

        boolean existe = repository.existsById(admin.getId());

        assertThat(existe).isTrue();
    }

    @Test
    void findAll_deberiaRetornarTodosLosAdministradores() {
        repository.save(new Administrador("Admin1", "admin1@test.com", "password", "SUPER"));
        repository.save(new Administrador("Admin2", "admin2@test.com", "password", "SUPER"));

        Iterable<Administrador> resultado = repository.findAll();

        assertThat(resultado).hasSizeGreaterThanOrEqualTo(2);
    }
}
