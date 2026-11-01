package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Notificacion;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-09: Pruebas de integración de {@link NotificacionRepository}.
 *
 * <p>Valida listado de notificaciones por destinatario y conteo de no leídas.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NotificacionRepositoryIntegrationTest {

    @Autowired
    private NotificacionRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = usuarioRepository.save(new Trabajador("Trabajador", "trabajador@test.com", "password"));
    }

    @Test
    void findByDestinatarioIdOrderByFechaCreacionDesc_deberiaRetornarNotificacionesDelUsuario() {
        Notificacion n1 = new Notificacion(usuario, "Notificacion 1", "Mensaje 1", Notificacion.TIPO_SISTEMA, null);
        Notificacion n2 = new Notificacion(usuario, "Notificacion 2", "Mensaje 2", Notificacion.TIPO_SISTEMA, null);
        repository.save(n1);
        repository.save(n2);

        List<Notificacion> resultado = repository.findByDestinatarioIdOrderByFechaCreacionDesc(usuario.getId());

        assertThat(resultado).hasSize(2);
    }

    @Test
    void countByDestinatarioIdAndLeidaFalse_deberiaContarNoLeidas() {
        Notificacion n1 = new Notificacion(usuario, "No leída 1", "Mensaje", Notificacion.TIPO_SISTEMA, null);
        n1.setLeida(false);
        Notificacion n2 = new Notificacion(usuario, "No leída 2", "Mensaje", Notificacion.TIPO_SISTEMA, null);
        n2.setLeida(false);
        Notificacion n3 = new Notificacion(usuario, "Leída", "Mensaje", Notificacion.TIPO_SISTEMA, null);
        n3.setLeida(true);
        repository.save(n1);
        repository.save(n2);
        repository.save(n3);

        long noLeidas = repository.countByDestinatarioIdAndLeidaFalse(usuario.getId());

        assertThat(noLeidas).isEqualTo(2);
    }
}
