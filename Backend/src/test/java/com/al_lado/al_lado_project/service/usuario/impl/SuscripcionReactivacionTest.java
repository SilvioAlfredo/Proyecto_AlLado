package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.mappers.usuario.SuscripcionMapper;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** AL-SEC-009: un POST propio nunca puede conceder PRO ni reactivar beneficios pagados. */
@ExtendWith(MockitoExtension.class)
class SuscripcionReactivacionTest {

    @Mock private SuscripcionRepository repository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private OportunidadLaboralRepository oportunidadRepository;
    @Mock private SuscripcionMapper mapper;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void usuarioNoPuedeAutootorgarseProMediantePost() {
        Trabajador usuario = new Trabajador();
        usuario.setId(7L);
        usuario.setEmail("ana@example.com");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuario));

        SuscripcionDTO dto = new SuscripcionDTO();
        UsuarioResumenDTO resumen = new UsuarioResumenDTO();
        resumen.setId(7L);
        dto.setUsuario(resumen);
        dto.setNivel("PRO");

        SuscripcionServiceImpl service = new SuscripcionServiceImpl(repository, usuarioRepository,
                oportunidadRepository, mapper,
                Clock.fixed(Instant.parse("2025-05-10T12:00:00Z"), ZoneOffset.UTC));

        assertThrows(AccessDeniedException.class, () -> service.crear(dto));
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
