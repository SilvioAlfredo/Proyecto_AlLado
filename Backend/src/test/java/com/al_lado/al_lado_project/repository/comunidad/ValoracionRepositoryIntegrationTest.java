package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;

import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
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
 * HU-06 y HU-12: Pruebas de integración de {@link ValoracionRepository}.
 *
 * <p>Valida conteo de valoraciones, promedio de puntuación y regla de una valoración por pareja.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
class ValoracionRepositoryIntegrationTest {

    @Autowired
    private ValoracionRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Trabajador trabajador;
    private Empleador empleador;

    @BeforeEach
    void setUp() {
        trabajador = usuarioRepository.save(new Trabajador("Trabajador", "trabajador@test.com", "password"));
        empleador = usuarioRepository.save(new Empleador("Empleador", "empleador@test.com", "password"));
    }

    @Test
    void countByTrabajadorId_deberiaContarValoraciones() {
        repository.save(new Valoracion(5, "Excelente", empleador, trabajador));
        repository.save(new Valoracion(4, "Muy bien", empleador, trabajador));

        Long cantidad = repository.countByTrabajadorId(trabajador.getId());

        assertThat(cantidad).isEqualTo(2);
    }

    @Test
    void calcularPromedioPuntuacionPorTrabajadorId_deberiaCalcularPromedio() {
        repository.save(new Valoracion(5, "Excelente", empleador, trabajador));
        repository.save(new Valoracion(3, "Regular", empleador, trabajador));

        Double promedio = repository.calcularPromedioPuntuacionPorTrabajadorId(trabajador.getId());

        assertThat(promedio).isEqualTo(4.0);
    }

    @Test
    void calcularPromedioPuntuacionPorTrabajadorId_deberiaRetornarNullSinValoraciones() {
        Double promedio = repository.calcularPromedioPuntuacionPorTrabajadorId(trabajador.getId());

        assertThat(promedio).isNull();
    }

    @Test
    void existsByAutorIdAndTrabajadorId_deberiaDetectarDuplicado() {
        repository.save(new Valoracion(5, "Excelente", empleador, trabajador));

        boolean existe = repository.existsByAutorIdAndTrabajadorId(empleador.getId(), trabajador.getId());

        assertThat(existe).isTrue();
    }

    @Test
    void findByTrabajadorId_deberiaRetornarValoracionesDelTrabajador() {
        repository.save(new Valoracion(5, "Excelente", empleador, trabajador));

        List<Valoracion> resultado = repository.findByTrabajadorId(trabajador.getId());

        assertThat(resultado).hasSize(1);
    }
}
