package com.al_lado.al_lado_project.service.comunidad.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.al_lado.al_lado_project.dto.comunidad.PerfilEmpresarialDTO;
import com.al_lado.al_lado_project.entity.comunidad.PerfilEmpresarial;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.mappers.comunidad.PerfilEmpresarialMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import com.al_lado.al_lado_project.repository.comunidad.PerfilEmpresarialRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.service.comunidad.PerfilEmpresarialService;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;

@ExtendWith(MockitoExtension.class)
class PerfilEmpresarialServiceImplTest {

    @Mock private PerfilEmpresarialRepository perfilRepository;
    @Mock private EmpleadorRepository empleadorRepository;
    @Mock private AdministradorRepository administradorRepository;
    @Mock private SuscripcionService suscripcionService;
    private final PerfilEmpresarialMapper mapper = new PerfilEmpresarialMapper(new UsuarioResumenMapper());
    private PerfilEmpresarialService service;

    private Empleador empleador;
    private PerfilEmpresarial perfil;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new PerfilEmpresarialServiceImpl(
                perfilRepository, empleadorRepository, administradorRepository, mapper, suscripcionService);

        empleador = new Empleador();
        empleador.setId(1L);
        empleador.setNombre("Empleador PRO");
        empleador.setEmail("emp@test.com");
        empleador.setPassword("x");
        // La sesion es del propio empleador titular del perfil.
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(empleador, null, empleador.getAuthorities()));

        perfil = new PerfilEmpresarial();
        perfil.setId(1L);
        perfil.setEmpleador(empleador);
        perfil.setNombreEmpresa("Mi Empresa");
        perfil.setVerificado(false);
    }

    @AfterEach
    void limpiarContexto() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void guardar_deberiaCrearPerfilCuandoEmpleadorTienePRO() {
        // Dado
        when(suscripcionService.esProVigente(1L)).thenReturn(true);
        when(empleadorRepository.findById(1L)).thenReturn(Optional.of(empleador));
        when(perfilRepository.findByCuit("20-12345678-9")).thenReturn(Optional.empty());
        when(perfilRepository.findByEmpleadorId(1L)).thenReturn(Optional.empty());
        when(perfilRepository.save(any(PerfilEmpresarial.class))).thenReturn(perfil);

        // Cuando
        PerfilEmpresarialDTO resultado = service.guardar(1L, perfilDTO());

        // Entonces
        assertNotNull(resultado);
        verify(perfilRepository, times(1)).save(any(PerfilEmpresarial.class));
    }

    @Test
    void guardar_deberiaRechazarCuandoEmpleadorNoTienePRO() {
        // Dado
        when(suscripcionService.esProVigente(1L)).thenReturn(false);

        // Cuando
        PerfilEmpresarialDTO dto = new PerfilEmpresarialDTO();
        dto.setNombreEmpresa("Mi Empresa");
        assertThrows(BusinessRuleException.class, () -> service.guardar(1L, dto));

        // Entonces
        verify(perfilRepository, never()).save(any(PerfilEmpresarial.class));
    }

    @Test
    void verificar_deberiaMarcarComoVerificadoCuandoEsAdmin() {
        // Dado: la sesion es del administrador 99 (supervisor indicado).
        Administrador administrador = new Administrador();
        administrador.setId(99L);
        administrador.setNombre("Admin Test");
        administrador.setEmail("admin@test.com");
        administrador.setPassword("x");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(administrador, null, administrador.getAuthorities()));
        when(perfilRepository.findById(1L)).thenReturn(Optional.of(perfil));
        when(administradorRepository.findById(99L)).thenReturn(Optional.of(administrador));
        when(perfilRepository.save(any(PerfilEmpresarial.class))).thenReturn(perfil);

        // Cuando
        service.verificar(1L, 99L);

        // Entonces
        assertTrue(perfil.isVerificado());
        verify(perfilRepository, times(1)).save(perfil);
    }

    private PerfilEmpresarialDTO perfilDTO() {
        PerfilEmpresarialDTO dto = new PerfilEmpresarialDTO();
        dto.setNombreEmpresa("Mi Empresa");
        dto.setCuit("20-12345678-9");
        return dto;
    }
}
