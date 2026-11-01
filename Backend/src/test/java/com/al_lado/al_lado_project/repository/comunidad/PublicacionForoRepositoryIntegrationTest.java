package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-13: Pruebas de integración de {@link PublicacionForoRepository}.
 *
 * <p>Valida listado de publicaciones del foro y por autor.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PublicacionForoRepositoryIntegrationTest {

    @Autowired
    private PublicacionForoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario autor;

    @BeforeEach
    void setUp() {
        autor = usuarioRepository.save(new Trabajador("Autor", "autor@test.com", "password"));
    }

    @Test
    void findAll_deberiaRetornarPublicacionesPaginadas() {
        PublicacionForo p1 = new PublicacionForo("Titulo 1", "Contenido 1", autor);
        PublicacionForo p2 = new PublicacionForo("Titulo 2", "Contenido 2", autor);
        repository.save(p1);
        repository.save(p2);

        List<PublicacionForo> resultado = repository.findAll(PageRequest.of(0, 10)).getContent();

        assertThat(resultado).hasSize(2);
    }

    @Test
    void findByAutorId_deberiaRetornarPublicacionesDelAutor() {
        PublicacionForo p = new PublicacionForo("Titulo", "Contenido", autor);
        repository.save(p);

        List<PublicacionForo> resultado = repository.findByAutorId(autor.getId(), PageRequest.of(0, 10)).getContent();

        assertThat(resultado).hasSize(1);
    }
}
