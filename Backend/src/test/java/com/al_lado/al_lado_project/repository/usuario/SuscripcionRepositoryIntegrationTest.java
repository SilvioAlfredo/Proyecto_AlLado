package com.al_lado.al_lado_project.repository.usuario;

import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-16 y HU-12: Pruebas de integración de {@link SuscripcionRepository}.
 *
 * <p>Valida búsqueda por usuario, conteo de PRO vigentes y suscripción vigente.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
class SuscripcionRepositoryIntegrationTest {

    @Autowired
    private SuscripcionRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = usuarioRepository.save(new Trabajador("Trabajador", "trabajador@test.com", "password"));
    }

    @Test
    void findByUsuarioId_deberiaRetornarSuscripcionDelUsuario() {
        Suscripcion suscripcion = new Suscripcion(usuario, "PRO", LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), true);
        repository.save(suscripcion);

        Optional<Suscripcion> resultado = repository.findByUsuarioId(usuario.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNivel()).isEqualTo("PRO");
    }

    @Test
    void existsByUsuarioId_deberiaRetornarTrueSiTieneSuscripcion() {
        Suscripcion suscripcion = new Suscripcion(usuario, "BASICO", LocalDate.of(2026, 1, 1), null, true);
        repository.save(suscripcion);

        boolean existe = repository.existsByUsuarioId(usuario.getId());

        assertThat(existe).isTrue();
    }

    @Test
    void countProVigentes_deberiaContarPROVigentes() {
        Suscripcion proVigente = new Suscripcion(usuario, "PRO", LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), true);
        repository.save(proVigente);

        long cantidad = repository.countProVigentes(LocalDate.of(2026, 6, 1));

        assertThat(cantidad).isGreaterThanOrEqualTo(1);
    }

    @Test
    void findSuscripcionVigentePorUsuarioId_deberiaRetornarSuscripcionVigente() {
        Suscripcion suscripcion = new Suscripcion(usuario, "PRO", LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), true);
        repository.save(suscripcion);

        Optional<Suscripcion> resultado = repository.findSuscripcionVigentePorUsuarioId(
                usuario.getId(), LocalDate.of(2026, 6, 1));

        assertThat(resultado).isPresent();
        assertThat(resultado.get().isActiva()).isTrue();
    }
}
