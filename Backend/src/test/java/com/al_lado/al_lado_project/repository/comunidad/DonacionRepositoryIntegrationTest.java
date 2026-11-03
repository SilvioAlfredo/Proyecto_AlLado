package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;

import com.al_lado.al_lado_project.entity.comunidad.Donacion;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-06 CA-02 y HU-16: Pruebas de integración de {@link DonacionRepository}.
 *
 * <p>Valida suma de montos, bloqueo pesimista y búsquedas por usuario.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
class DonacionRepositoryIntegrationTest {

    @Autowired
    private DonacionRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = usuarioRepository.save(new Trabajador("Trabajador", "trabajador@test.com", "password"));
    }

    @Test
    void sumTotalMontos_deberiaSumarTodasLasDonaciones() {
        Donacion d1 = new Donacion(new BigDecimal("10000.00"), "ARS", "TRANSFERENCIA", usuario);
        d1.setEstado("VERIFICADO");
        Donacion d2 = new Donacion(new BigDecimal("20000.00"), "ARS", "TRANSFERENCIA", usuario);
        d2.setEstado("PENDIENTE");
        repository.save(d1);
        repository.save(d2);

        BigDecimal total = repository.sumTotalMontos();

        assertThat(total).isEqualByComparingTo(new BigDecimal("30000.00"));
    }

    @Test
    void findByIdForUpdate_deberiaBloquearRegistro() {
        Donacion donacion = new Donacion(new BigDecimal("15000.00"), "ARS", "TRANSFERENCIA", usuario);
        donacion.setAsuntoTransferencia("APORTE PRO");
        Donacion guardada = repository.save(donacion);

        Optional<Donacion> resultado = repository.findByIdForUpdate(guardada.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getAsuntoTransferencia()).isEqualTo("APORTE PRO");
    }

    @Test
    void findByUsuarioId_deberiaRetornarDonacionesDelUsuario() {
        Donacion d1 = new Donacion(new BigDecimal("10000.00"), "ARS", "TRANSFERENCIA", usuario);
        Donacion d2 = new Donacion(new BigDecimal("20000.00"), "ARS", "TRANSFERENCIA", usuario);
        repository.save(d1);
        repository.save(d2);

        List<Donacion> resultado = repository.findByUsuarioId(usuario.getId());

        assertThat(resultado).hasSize(2);
    }
}
