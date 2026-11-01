package com.al_lado.al_lado_project.repository.perfil;

import com.al_lado.al_lado_project.entity.perfil.HorarioDisponibilidad;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-10: Pruebas de integración de {@link HorarioDisponibilidadRepository}.
 *
 * <p>Valida búsquedas de horarios por trabajador y día de la semana.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HorarioDisponibilidadRepositoryIntegrationTest {

    @Autowired
    private HorarioDisponibilidadRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Trabajador trabajador;

    @BeforeEach
    void setUp() {
        trabajador = usuarioRepository.save(new Trabajador("Trabajador", "trabajador@test.com", "password"));
    }

    @Test
    void findByTrabajadorId_deberiaRetornarHorariosDelTrabajador() {
        HorarioDisponibilidad h1 = new HorarioDisponibilidad("Lunes", LocalTime.of(9, 0), LocalTime.of(12, 0), trabajador);
        HorarioDisponibilidad h2 = new HorarioDisponibilidad("Martes", LocalTime.of(14, 0), LocalTime.of(17, 0), trabajador);
        repository.save(h1);
        repository.save(h2);

        List<HorarioDisponibilidad> resultado = repository.findByTrabajadorId(trabajador.getId());

        assertThat(resultado).hasSize(2);
    }

    @Test
    void findByTrabajadorIdAndDiaSemanaIgnoreCase_deberiaFiltrarPorDia() {
        HorarioDisponibilidad h1 = new HorarioDisponibilidad("Lunes", LocalTime.of(9, 0), LocalTime.of(12, 0), trabajador);
        HorarioDisponibilidad h2 = new HorarioDisponibilidad("Martes", LocalTime.of(14, 0), LocalTime.of(17, 0), trabajador);
        repository.save(h1);
        repository.save(h2);

        List<HorarioDisponibilidad> resultado = repository.findByTrabajadorIdAndDiaSemanaIgnoreCase(trabajador.getId(), "lunes");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDiaSemana()).isEqualTo("Lunes");
    }
}
