package com.al_lado.al_lado_project.repository.trabajo;

import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.trabajo.Etiqueta;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.trabajo.EtiquetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-01 y HU-07: Pruebas de integración de {@link OportunidadLaboralRepository}.
 *
 * <p>Valida consultas de ofertas activas, por autor, cupo mensual, tendencias por
 * etiqueta, búsqueda por texto y por modalidad.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
class OportunidadLaboralRepositoryIntegrationTest {

    @Autowired
    private OportunidadLaboralRepository repository;

    @Autowired
    private EmpleadorRepository empleadorRepository;

    @Autowired
    private EtiquetaRepository etiquetaRepository;

    private Empleador empleador;
    private Etiqueta etiquetaPintura;
    private Etiqueta etiquetaElectricidad;

    @BeforeEach
    void setUp() {
        empleador = empleadorRepository.save(new Empleador("Empleador Test", "empleador@test.com", "password"));
        etiquetaPintura = etiquetaRepository.save(new Etiqueta("Pintura"));
        etiquetaElectricidad = etiquetaRepository.save(new Etiqueta("Electricidad"));
    }

    @Test
    void findByActivaTrue_deberiaRetornarSoloOfertasActivas() {
        OportunidadLaboral activa = new OportunidadLaboral("Oferta Activa", "Descripcion", empleador, "Barrio", "PRESENCIAL", "$10000");
        activa.setActiva(true);
        OportunidadLaboral inactiva = new OportunidadLaboral("Oferta Cerrada", "Descripcion", empleador, "Barrio", "REMOTO", "$20000");
        inactiva.setActiva(false);
        repository.save(activa);
        repository.save(inactiva);

        List<OportunidadLaboral> resultado = repository.findByActivaTrue();

        // La base QA trae ofertas de seed: se valida el contrato del método
        // (solo devuelve activas) en lugar de un conteo exacto.
        assertThat(resultado).allMatch(OportunidadLaboral::isActiva);
        assertThat(resultado).extracting(OportunidadLaboral::getTitulo)
                .contains("Oferta Activa")
                .doesNotContain("Oferta Cerrada");
    }

    @Test
    void findByAutorId_deberiaRetornarOfertasDelEmpleador() {
        OportunidadLaboral o1 = new OportunidadLaboral("Oferta 1", "D1", empleador, "Barrio", "PRESENCIAL", "$10000");
        OportunidadLaboral o2 = new OportunidadLaboral("Oferta 2", "D2", empleador, "Barrio", "REMOTO", "$20000");
        repository.save(o1);
        repository.save(o2);

        List<OportunidadLaboral> resultado = repository.findByAutorId(empleador.getId());

        assertThat(resultado).hasSize(2);
    }

    @Test
    void countByAutorIdAndActivaAndFechaCreacionBetween_deberiaContarOfertasDelMes() {
        LocalDateTime inicioMes = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        LocalDateTime finMes = LocalDateTime.of(2026, 1, 31, 23, 59, 59);

        OportunidadLaboral oferta = new OportunidadLaboral("Oferta Mes", "D", empleador, "Barrio", "PRESENCIAL", "$10000");
        oferta.setActiva(true);
        oferta.setFechaCreacion(LocalDateTime.of(2026, 1, 15, 10, 0));
        repository.save(oferta);

        long cantidad = repository.countByAutorIdAndActivaAndFechaCreacionBetween(
                empleador.getId(), true, inicioMes, finMes);

        assertThat(cantidad).isGreaterThanOrEqualTo(1);
    }

    @Test
    void findTendenciasPorEtiqueta_deberiaRetornarAgrupadasPorEtiqueta() {
        OportunidadLaboral o1 = new OportunidadLaboral("Oferta Pintura", "D", empleador, "Barrio", "PRESENCIAL", "$10000");
        o1.addEtiqueta(etiquetaPintura);
        OportunidadLaboral o2 = new OportunidadLaboral("Oferta Electricidad", "D", empleador, "Barrio", "REMOTO", "$20000");
        o2.addEtiqueta(etiquetaElectricidad);
        repository.save(o1);
        repository.save(o2);

        List<Object[]> resultado = repository.findTendenciasPorEtiqueta();

        assertThat(resultado).isNotEmpty();
        boolean hayPintura = resultado.stream()
                .anyMatch(row -> "Pintura".equals(row[0]));
        assertThat(hayPintura).isTrue();
    }

    @Test
    void buscarActivasPorTermino_deberiaBuscarEnTituloYContenido() {
        OportunidadLaboral oferta = new OportunidadLaboral("Fontanero urgente", "Reparacion de tuberias", empleador, "Barrio", "PRESENCIAL", "$15000");
        oferta.setActiva(true);
        repository.save(oferta);

        List<OportunidadLaboral> resultado = repository.buscarActivasPorTermino("fontanero");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Fontanero urgente");
    }

    @Test
    void findByEtiquetaNombre_deberiaBuscarPorEtiquetaExacta() {
        OportunidadLaboral oferta = new OportunidadLaboral("Oferta", "D", empleador, "Barrio", "PRESENCIAL", "$10000");
        oferta.addEtiqueta(etiquetaPintura);
        repository.save(oferta);

        List<OportunidadLaboral> resultado = repository.findByEtiquetaNombre("Pintura");

        assertThat(resultado).hasSize(1);
    }

    @Test
    void findByModalidad_deberiaFiltrarPorModalidad() {
        OportunidadLaboral oferta = new OportunidadLaboral("Oferta Remoto", "D", empleador, "Barrio", "REMOTO", "$10000");
        oferta.setActiva(true);
        repository.save(oferta);

        List<OportunidadLaboral> resultado = repository.findByModalidad("REMOTO");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getModalidad()).isEqualTo("REMOTO");
    }
}
