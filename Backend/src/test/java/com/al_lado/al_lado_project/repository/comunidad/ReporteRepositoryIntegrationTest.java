package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;

import com.al_lado.al_lado_project.entity.comunidad.Reporte;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-13 y HU-18: Pruebas de integración de {@link ReporteRepository}.
 *
 * <p>Valida listado de reportes por estado y por sector.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
class ReporteRepositoryIntegrationTest {

    @Autowired
    private ReporteRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario reportante;
    private Usuario reportado;

    @BeforeEach
    void setUp() {
        reportante = usuarioRepository.save(new Trabajador("Reportante", "reportante@test.com", "password"));
        reportado = usuarioRepository.save(new Trabajador("Reportado", "reportado@test.com", "password"));
    }

    @Test
    void findByEstadoIgnoreCase_deberiaFiltrarPorEstado() {
        Reporte r1 = new Reporte("Fraude", "Descripcion", "Centro", reportante, reportado);
        r1.setEstado("PENDIENTE");
        Reporte r2 = new Reporte("Acoso", "Descripcion", "Norte", reportante, reportado);
        r2.setEstado("RESUELTO");
        repository.save(r1);
        repository.save(r2);

        List<Reporte> resultado = repository.findByEstadoIgnoreCase("PENDIENTE", PageRequest.of(0, 10)).getContent();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    void findBySectorIgnoreCase_deberiaFiltrarPorSector() {
        Reporte r1 = new Reporte("Fraude", "Descripcion", "Centro", reportante, reportado);
        Reporte r2 = new Reporte("Acoso", "Descripcion", "Norte", reportante, reportado);
        repository.save(r1);
        repository.save(r2);

        List<Reporte> resultado = repository.findBySectorIgnoreCase("Centro", PageRequest.of(0, 10)).getContent();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getSector()).isEqualTo("Centro");
    }
}
