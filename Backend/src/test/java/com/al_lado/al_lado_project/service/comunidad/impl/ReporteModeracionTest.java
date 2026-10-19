package com.al_lado.al_lado_project.service.comunidad.impl;
import com.al_lado.al_lado_project.entity.comunidad.Reporte;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.mappers.comunidad.ReporteMapper;
import com.al_lado.al_lado_project.repository.comunidad.ReporteRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ReporteModeracionTest {
    @Mock ReporteRepository repository;
    @Mock UsuarioRepository usuarios;
    @Mock AdministradorRepository admins;
    @Mock ReporteMapper mapper;
    ReporteServiceImpl service;
    @BeforeEach void setup() { service = new ReporteServiceImpl(repository, usuarios, admins, mapper); }
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    @Test void asignaAlAdministradorAutenticadoSinIdEnLaPeticion() {
        var report = new Reporte(); report.setEstado("PENDIENTE");
        var admin = new Administrador(); admin.setId(9L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities()));
        when(repository.findById(1L)).thenReturn(Optional.of(report));
        when(admins.findById(9L)).thenReturn(Optional.of(admin));
        when(repository.save(report)).thenReturn(report);
        service.resolver(1L, null, "EN_REVISION");
        assertEquals("EN_REVISION", report.getEstado()); assertSame(admin, report.getModerador());
        service.resolver(1L, null, "RESUELTO");
        assertEquals("RESUELTO", report.getEstado()); verify(repository, times(2)).save(report);
    }
    @Test void estadosFinalesNoSeReabren() {
        var report = new Reporte(); when(repository.findById(1L)).thenReturn(Optional.of(report));
        for (var state : new String[]{"RESUELTO", "DESCARTADO"}) {
            report.setEstado(state);
            assertThrows(BusinessRuleException.class, () -> service.resolver(1L, null, "EN_REVISION"));
            assertEquals(state, report.getEstado());
        }
        verify(repository, never()).save(any()); verifyNoInteractions(admins);
    }
    @Test void estadoInvalidoNoSeGuarda() {
        var report = new Reporte(); report.setEstado("PENDIENTE");
        when(repository.findById(1L)).thenReturn(Optional.of(report));
        assertThrows(BusinessRuleException.class, () -> service.resolver(1L, null, "ABIERTO"));
        verify(repository, never()).save(any());
    }
    @Test void sinModeradorAutenticadoNoSeGuarda() {
        var report = new Reporte(); report.setEstado("PENDIENTE");
        when(repository.findById(1L)).thenReturn(Optional.of(report));
        assertThrows(BusinessRuleException.class, () -> service.resolver(1L, null, "EN_REVISION"));
        verify(repository, never()).save(any());
    }
}
