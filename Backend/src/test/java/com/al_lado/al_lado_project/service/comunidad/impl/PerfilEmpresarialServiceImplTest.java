package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.PerfilEmpresarialDTO;
import com.al_lado.al_lado_project.entity.comunidad.PerfilEmpresarial;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.PerfilEmpresarialMapper;
import com.al_lado.al_lado_project.repository.comunidad.PerfilEmpresarialRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.PerfilEmpresarialService;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * HU-14: Publicar el perfil de mi comercio y obtener el Sello de Confianza.
 *
 * <p>Pruebas unitarias de {@link PerfilEmpresarialServiceImpl}. Valida crear/
 * actualizar perfil empresarial, requisito de PRO vigente, unicidad de CUIT,
 * y verificacion por administrador.</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PerfilEmpresarialServiceImplTest {

    @Mock
    private PerfilEmpresarialRepository repository;

    @Mock
    private EmpleadorRepository empleadorRepository;

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private PerfilEmpresarialMapper mapper;

    @Mock
    private SuscripcionService suscripcionService;

    private PerfilEmpresarialService service;

    private Empleador empleador;
    private PerfilEmpresarialDTO dto;
    private PerfilEmpresarial perfil;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new PerfilEmpresarialServiceImpl(
                repository, empleadorRepository, administradorRepository, mapper, suscripcionService);

        empleador = new Empleador("Empleador Demo", "empleador@demo.com", "password");
        empleador.setId(1L);

        dto = new PerfilEmpresarialDTO();
        dto.setNombreEmpresa("Mi Comercio");
        dto.setCuit("20-12345678-9");
        dto.setDescripcion("Descripcion del comercio");
        dto.setSitioWeb("https://comercio.com");
        dto.setUrlLogo("https://comercio.com/logo.png");

        perfil = new PerfilEmpresarial();
        perfil.setId(1L);
        perfil.setEmpleador(empleador);
    }

    private void autenticarComoEmpleador() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(empleador, null, empleador.getAuthorities()));
    }

    private void autenticarComoAdmin() {
        Administrador admin = new Administrador("Admin", "admin@demo.com", "password", "SUPER");
        admin.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities()));
    }

    private void autenticarComoOtro() {
        Empleador otro = new Empleador("Otro", "otro@demo.com", "password");
        otro.setId(2L);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(otro, null, otro.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void guardar_deberiaCrearPerfilEmpresarialConPROVigente() {
        autenticarComoEmpleador();
        when(suscripcionService.esProVigente(1L)).thenReturn(true);
        when(empleadorRepository.findById(1L)).thenReturn(Optional.of(empleador));
        when(repository.findByEmpleadorId(1L)).thenReturn(Optional.empty());
        when(mapper.toEntity(dto)).thenReturn(perfil);
        when(repository.save(perfil)).thenReturn(perfil);
        when(mapper.toDTO(perfil)).thenReturn(dto);

        PerfilEmpresarialDTO resultado = service.guardar(1L, dto);

        assertNotNull(resultado);
        assertEquals("Mi Comercio", resultado.getNombreEmpresa());
        verify(repository, times(1)).save(perfil);
    }

    @Test
    void guardar_deberiaRechazarSiNoTienePROVigente() {
        autenticarComoEmpleador();
        when(suscripcionService.esProVigente(1L)).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> service.guardar(1L, dto));
        verify(repository, never()).save(any());
    }

    @Test
    void guardar_deberiaRechazarSiCUITYaRegistradoPorOtroEmpleador() {
        autenticarComoEmpleador();
        when(suscripcionService.esProVigente(1L)).thenReturn(true);
        when(empleadorRepository.findById(1L)).thenReturn(Optional.of(empleador));
        when(repository.findByEmpleadorId(1L)).thenReturn(Optional.empty());

        PerfilEmpresarial existente = new PerfilEmpresarial();
        existente.setId(2L);
        Empleador otro = new Empleador("Otro", "otro@demo.com", "password");
        otro.setId(2L);
        existente.setEmpleador(otro);
        when(repository.findByCuit("20-12345678-9")).thenReturn(Optional.of(existente));

        assertThrows(DuplicateResourceException.class, () -> service.guardar(1L, dto));
    }

    @Test
    void guardar_deberiaRechazarAccesoAjeno() {
        autenticarComoOtro();

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.guardar(1L, dto));
    }

    @Test
    void verificar_deberiaMarcarPerfilComoVerificadoPorAdmin() {
        autenticarComoAdmin();
        when(repository.findById(1L)).thenReturn(Optional.of(perfil));
        Administrador admin = new Administrador();
        admin.setId(1L);
        when(administradorRepository.findById(1L)).thenReturn(Optional.of(admin));
        perfil.setVerificado(false);
        when(repository.save(perfil)).thenReturn(perfil);
        when(mapper.toDTO(perfil)).thenReturn(dto);

        PerfilEmpresarialDTO resultado = service.verificar(1L, 1L);

        assertTrue(perfil.isVerificado());
        verify(repository, times(1)).save(perfil);
    }

    @Test
    void verificar_deberiaRechazarSiNoEsAdmin() {
        autenticarComoOtro();

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> service.verificar(1L, 1L));
    }

    @Test
    void verificar_deberiaRechazarSiSupervisorNoCoincide() {
        autenticarComoEmpleador();

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> service.verificar(1L, 2L));
    }

    @Test
    void verificar_deberiaRechazarSiPerfilNoExiste() {
        autenticarComoAdmin();
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.verificar(999L, 1L));
    }

    @Test
    void obtenerPorEmpleador_deberiaRetornarPerfilExistente() {
        when(repository.findByEmpleadorId(1L)).thenReturn(Optional.of(perfil));
        when(mapper.toDTO(perfil)).thenReturn(dto);

        PerfilEmpresarialDTO resultado = service.obtenerPorEmpleador(1L);

        assertNotNull(resultado);
        assertEquals("Mi Comercio", resultado.getNombreEmpresa());
    }

    @Test
    void obtenerPorEmpleador_deberiaRechazarSiNoExistePerfil() {
        when(repository.findByEmpleadorId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.obtenerPorEmpleador(1L));
    }
}
