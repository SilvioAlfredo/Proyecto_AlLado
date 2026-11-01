package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.PerfilEmpresarial;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-14: Pruebas de integración de {@link PerfilEmpresarialRepository}.
 *
 * <p>Valida búsquedas por empleador y por CUIT.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PerfilEmpresarialRepositoryIntegrationTest {

    @Autowired
    private PerfilEmpresarialRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Empleador empleador;

    @BeforeEach
    void setUp() {
        empleador = usuarioRepository.save(new Empleador("Empleador", "empleador@test.com", "password"));
    }

    @Test
    void findByEmpleadorId_deberiaRetornarPerfilDelEmpleador() {
        PerfilEmpresarial perfil = new PerfilEmpresarial(
                "Mi Empresa", "20-12345678-9", "Descripcion", "web.com", "logo.png", empleador);
        repository.save(perfil);

        Optional<PerfilEmpresarial> resultado = repository.findByEmpleadorId(empleador.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombreEmpresa()).isEqualTo("Mi Empresa");
    }

    @Test
    void findByCuit_deberiaRetornarPerfilPorCUIT() {
        PerfilEmpresarial perfil = new PerfilEmpresarial(
                "Mi Empresa", "20-12345678-9", "Descripcion", "web.com", "logo.png", empleador);
        repository.save(perfil);

        Optional<PerfilEmpresarial> resultado = repository.findByCuit("20-12345678-9");

        assertThat(resultado).isPresent();
    }

    @Test
    void findByCuit_deberiaRetornarVacioSiCUITNoExiste() {
        Optional<PerfilEmpresarial> resultado = repository.findByCuit("99-99999999-9");

        assertThat(resultado).isEmpty();
    }
}
