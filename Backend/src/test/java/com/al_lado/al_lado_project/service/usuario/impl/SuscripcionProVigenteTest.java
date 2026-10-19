package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.mappers.usuario.SuscripcionMapper;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/** Funciones PRO: solo PRO activa y vigente. */
@ExtendWith(MockitoExtension.class)
class SuscripcionProVigenteTest {

    @Mock
    private SuscripcionRepository repository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private OportunidadLaboralRepository oportunidadLaboralRepository;
    @Mock
    private SuscripcionMapper mapper;

    @InjectMocks
    private SuscripcionServiceImpl service;

    private Suscripcion pro(boolean activa, LocalDate fin) {
        Trabajador t = new Trabajador();
        t.setId(1L);
        return new Suscripcion(t, "PRO", LocalDate.now().minusDays(1), fin, activa);
    }

    @Test
    void proActivaVigenteEsTrue() {
        when(repository.findByUsuarioId(1L)).thenReturn(Optional.of(pro(true, LocalDate.now().plusDays(10))));
        assertTrue(service.esProVigente(1L));
    }

    @Test
    void proInactivaEsFalse() {
        when(repository.findByUsuarioId(1L)).thenReturn(Optional.of(pro(false, LocalDate.now().plusDays(10))));
        assertFalse(service.esProVigente(1L));
    }

    @Test
    void proVencidaEsFalse() {
        when(repository.findByUsuarioId(1L)).thenReturn(Optional.of(pro(true, LocalDate.now().minusDays(1))));
        assertFalse(service.esProVigente(1L));
    }

    @Test
    void sinSuscripcionEsFalse() {
        when(repository.findByUsuarioId(1L)).thenReturn(Optional.empty());
        assertFalse(service.esProVigente(1L));
    }

    @Test
    void basicoEsFalse() {
        Trabajador t = new Trabajador();
        t.setId(1L);
        when(repository.findByUsuarioId(1L)).thenReturn(
                Optional.of(new Suscripcion(t, "BASICO", LocalDate.now().minusDays(1), null, true)));
        assertFalse(service.esProVigente(1L));
    }
}
