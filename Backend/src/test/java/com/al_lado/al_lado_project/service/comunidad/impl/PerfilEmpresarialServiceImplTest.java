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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.springframework.security.access.AccessDeniedException;

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

    @Test
    void guardar_deberiaCrearPerfilEmpresarialConPROVigente() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);
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
    }

    @Test
    void guardar_deberiaRechazarSiNoTienePROVigente() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);
            when(suscripcionService.esProVigente(1L)).thenReturn(false);

            assertThrows(BusinessRuleException.class, () -> service.guardar(1L, dto));
            verify(repository, never()).save(any());
        }
    }

    @Test
    void guardar_deberiaRechazarSiCUITYaRegistradoPorOtroEmpleador() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);
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
    }

    @Test
    void guardar_deberiaRechazarAccesoAjeno() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::currentUserId).thenReturn(2L);

            assertThrows(org.springframework.security.access.AccessDeniedException.class,
                    () -> service.guardar(1L, dto));
        }
    }

    @Test
    void verificar_deberiaMarcarPerfilComoVerificadoPorAdmin() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::isAdmin()).thenReturn(true);
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);
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
    }

    @Test
    void verificar_deberiaRechazarSiNoEsAdmin() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::isAdmin()).thenReturn(false);

            assertThrows(AccessDeniedException.class, () -> service.verificar(1L, 1L));
        }
    }

    @Test
    void verificar_deberiaRechazarSiSupervisorNoCoincide() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::isAdmin()).thenReturn(true);
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);

            assertThrows(AccessDeniedException.class, () -> service.verificar(1L, 2L));
        }
    }

    @Test
    void verificar_deberiaRechazarSiPerfilNoExiste() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::isAdmin()).thenReturn(true);
            mocked.when(SecurityUtils::currentUserId).thenReturn(1L);
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.verificar(999L, 1L));
        }
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
