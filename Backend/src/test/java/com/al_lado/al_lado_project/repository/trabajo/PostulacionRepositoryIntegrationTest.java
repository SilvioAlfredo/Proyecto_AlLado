package com.al_lado.al_lado_project.repository.trabajo;

import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-02 y HU-12: Pruebas de integración de {@link PostulacionRepository}.
 *
 * <p>Valida consultas de postulaciones por autor, por oportunidad, conteos por
 * estado, agregaciones de ingresos y machine de estados.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
class PostulacionRepositoryIntegrationTest {

    @Autowired
    private PostulacionRepository repository;

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private EmpleadorRepository empleadorRepository;

    @Autowired
    private OportunidadLaboralRepository oportunidadRepository;

    private Trabajador trabajador;
    private Empleador empleador;
    private OportunidadLaboral oportunidad;

    @BeforeEach
    void setUp() {
        trabajador = trabajadorRepository.save(new Trabajador("Trabajador Test", "trabajador@test.com", "password"));
        empleador = empleadorRepository.save(new Empleador("Empleador Test", "empleador@test.com", "password"));
        oportunidad = new OportunidadLaboral("Oferta Test", "Descripcion", empleador, "Barrio", "PRESENCIAL", "$10000");
        oportunidad.setActiva(true);
        oportunidad = oportunidadRepository.save(oportunidad);
    }

    @Test
    void findByAutorId_deberiaRetornarPostulacionesDelTrabajador() {
        Postulacion p1 = new Postulacion("Postulacion 1", "Mensaje 1", trabajador, oportunidad, 10000.0);
        p1.setEstado(Postulacion.ESTADO_PENDIENTE);
        Postulacion p2 = new Postulacion("Postulacion 2", "Mensaje 2", trabajador, oportunidad, 12000.0);
        p2.setEstado(Postulacion.ESTADO_COMPLETADA);
        repository.save(p1);
        repository.save(p2);

        List<Postulacion> resultado = repository.findByAutorId(trabajador.getId());

        assertThat(resultado).hasSize(2);
    }

    @Test
    void countByAutorId_deberiaContarTodasLasPostulaciones() {
        Postulacion p1 = new Postulacion("P1", "M1", trabajador, oportunidad, 10000.0);
        p1.setEstado(Postulacion.ESTADO_PENDIENTE);
        Postulacion p2 = new Postulacion("P2", "M2", trabajador, oportunidad, 12000.0);
        p2.setEstado(Postulacion.ESTADO_COMPLETADA);
        repository.save(p1);
        repository.save(p2);

        long cantidad = repository.countByAutorId(trabajador.getId());

        assertThat(cantidad).isEqualTo(2);
    }

    @Test
    void countByAutorIdAndEstadoIgnoreCase_deberiaContarPorEstado() {
        Postulacion p1 = new Postulacion("P1", "M1", trabajador, oportunidad, 10000.0);
        p1.setEstado(Postulacion.ESTADO_COMPLETADA);
        Postulacion p2 = new Postulacion("P2", "M2", trabajador, oportunidad, 12000.0);
        p2.setEstado(Postulacion.ESTADO_COMPLETADA);
        Postulacion p3 = new Postulacion("P3", "M3", trabajador, oportunidad, 8000.0);
        p3.setEstado(Postulacion.ESTADO_PENDIENTE);
        repository.save(p1);
        repository.save(p2);
        repository.save(p3);

        long completadas = repository.countByAutorIdAndEstadoIgnoreCase(trabajador.getId(), Postulacion.ESTADO_COMPLETADA);
        long pendientes = repository.countByAutorIdAndEstadoIgnoreCase(trabajador.getId(), Postulacion.ESTADO_PENDIENTE);

        assertThat(completadas).isEqualTo(2);
        assertThat(pendientes).isEqualTo(1);
    }

    @Test
    void countByEstadoIgnoreCase_deberiaContarGlobalPorEstado() {
        Postulacion p1 = new Postulacion("P1", "M1", trabajador, oportunidad, 10000.0);
        p1.setEstado(Postulacion.ESTADO_COMPLETADA);
        repository.save(p1);

        long cantidad = repository.countByEstadoIgnoreCase(Postulacion.ESTADO_COMPLETADA);

        assertThat(cantidad).isGreaterThanOrEqualTo(1);
    }

    @Test
    void promedioRemuneracionPositiva_deberiaCalcularPromedio() {
        // La base QA trae postulaciones de seed: se limpia la tabla para un
        // promedio determinista (@DataJpaTest revierte todo al final del test).
        repository.deleteAll();
        Postulacion p1 = new Postulacion("P1", "M1", trabajador, oportunidad, 10000.0);
        Postulacion p2 = new Postulacion("P2", "M2", trabajador, oportunidad, 20000.0);
        repository.save(p1);
        repository.save(p2);

        Double promedio = repository.promedioRemuneracionPositiva();

        assertThat(promedio).isEqualTo(15000.0);
    }

    @Test
    void sumaRemuneracionCompletadas_deberiaSumarSoloCompletadas() {
        Postulacion p1 = new Postulacion("P1", "M1", trabajador, oportunidad, 10000.0);
        p1.setEstado(Postulacion.ESTADO_COMPLETADA);
        Postulacion p2 = new Postulacion("P2", "M2", trabajador, oportunidad, 20000.0);
        p2.setEstado(Postulacion.ESTADO_PENDIENTE);
        repository.save(p1);
        repository.save(p2);

        Double suma = repository.sumaRemuneracionCompletadas();

        assertThat(suma).isEqualTo(10000.0);
    }

    @Test
    void sumarImportesCompletadosPorTrabajador_deberiaSumarDelTrabajador() {
        Postulacion p1 = new Postulacion("P1", "M1", trabajador, oportunidad, 10000.0);
        p1.setEstado(Postulacion.ESTADO_COMPLETADA);
        repository.save(p1);

        Double suma = repository.sumarImportesCompletadosPorTrabajador(trabajador.getId());

        assertThat(suma).isEqualTo(10000.0);
    }

    @Test
    void findByOportunidadLaboralAutorId_deberiaRetornarPostulacionesDeOfertasDelEmpleador() {
        Postulacion p = new Postulacion("P", "M", trabajador, oportunidad, 10000.0);
        p.setEstado(Postulacion.ESTADO_PENDIENTE);
        repository.save(p);

        List<Postulacion> resultado = repository.findByOportunidadLaboralAutorId(empleador.getId());

        assertThat(resultado).hasSize(1);
    }

    @Test
    void existsByOportunidadLaboralIdAndAutorId_deberiaDetectarDuplicado() {
        Postulacion p = new Postulacion("P", "M", trabajador, oportunidad, 10000.0);
        p.setEstado(Postulacion.ESTADO_PENDIENTE);
        repository.save(p);

        boolean existe = repository.existsByOportunidadLaboralIdAndAutorId(oportunidad.getId(), trabajador.getId());

        assertThat(existe).isTrue();
    }

    @Test
    void findByOportunidadLaboralIdAndAutorId_deberiaRetornarPostulacionEspecifica() {
        Postulacion p = new Postulacion("P", "M", trabajador, oportunidad, 10000.0);
        p.setEstado(Postulacion.ESTADO_PENDIENTE);
        Postulacion guardada = repository.save(p);

        Optional<Postulacion> resultado = repository.findByOportunidadLaboralIdAndAutorId(oportunidad.getId(), trabajador.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(guardada.getId());
    }
}
