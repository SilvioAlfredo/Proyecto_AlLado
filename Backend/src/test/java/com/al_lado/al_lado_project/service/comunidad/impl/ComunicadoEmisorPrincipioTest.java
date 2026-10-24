package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.mappers.comunidad.ComunicadoMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import com.al_lado.al_lado_project.repository.comunidad.ComunicadoRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AL-SEC-010 (MEJ-016): las acciones administrativas se atribuyen al principal
 * autenticado, no al ID que llega en el cuerpo de la petición.
 */
@ExtendWith(MockitoExtension.class)
class ComunicadoEmisorPrincipioTest {

    @Mock
    private ComunicadoRepository repository;
    @Mock
    private AdministradorRepository administradorRepository;

    private ComunicadoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ComunicadoServiceImpl(repository, administradorRepository,
                new ComunicadoMapper(new UsuarioResumenMapper()));
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private void iniciarSesion(Administrador admin) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities()));
    }

    private static Administrador administrador(Long id) {
        Administrador admin = new Administrador();
        admin.setId(id);
        admin.setNombre("Admin " + id);
        admin.setEmail("admin" + id + "@al-lado.com");
        return admin;
    }

    private static ComunicadoDTO dtoDePrueba(Long emisorId) {
        ComunicadoDTO dto = new ComunicadoDTO();
        dto.setTitulo("Comunicado");
        dto.setContenido("Contenido");
        dto.setAlcance("Comunidad");
        if (emisorId != null) {
            UsuarioResumenDTO emisor = new UsuarioResumenDTO();
            emisor.setId(emisorId);
            dto.setEmisor(emisor);
        }
        return dto;
    }

    private void guardarLoQueSeValide() {
        when(repository.save(any(Comunicado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void elAdminExiste(Administrador admin) {
        when(administradorRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
    }

    @Test
    void elEmisorEsElAdministradorDeLaSesionAunqueElCuerpoPidaOtro() {
        Administrador deLaSesion = administrador(1L);
        iniciarSesion(deLaSesion);
        elAdminExiste(deLaSesion);
        guardarLoQueSeValide();

        ComunicadoDTO dto = service.crear(dtoDePrueba(2L));

        verify(administradorRepository).findById(1L);
        verify(administradorRepository, never()).findById(2L);
        assertEquals(1L, dto.getEmisor().getId());
    }

    @Test
    void elCuerpoPuedeOmitirElEmisorCuandoLaSesionLoTrae() {
        Administrador deLaSesion = administrador(1L);
        iniciarSesion(deLaSesion);
        elAdminExiste(deLaSesion);
        guardarLoQueSeValide();

        ComunicadoDTO dto = service.crear(dtoDePrueba(null));

        assertEquals(1L, dto.getEmisor().getId());
    }

    @Test
    void sinSesionNoSePuedeFirmarUnComunicado() {
        assertThrows(AccessDeniedException.class, () -> service.crear(dtoDePrueba(2L)));
        verify(repository, never()).save(any(Comunicado.class));
    }
}
