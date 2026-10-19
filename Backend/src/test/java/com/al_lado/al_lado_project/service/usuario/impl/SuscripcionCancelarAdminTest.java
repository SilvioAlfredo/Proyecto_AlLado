package com.al_lado.al_lado_project.service.usuario.impl;
import com.al_lado.al_lado_project.entity.usuario.*;
import com.al_lado.al_lado_project.mappers.usuario.SuscripcionMapper;
import com.al_lado.al_lado_project.repository.usuario.*;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class SuscripcionCancelarAdminTest {
    @Mock SuscripcionRepository repository;
    @Mock UsuarioRepository usuarios;
    @Mock OportunidadLaboralRepository ofertas;
    @Mock SuscripcionMapper mapper;
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    @Test void cancelarComoAdminConservaElRegistroYLaVigencia() {
        var admin = new Administrador(); admin.setId(9L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin,null,admin.getAuthorities()));
        var owner = new Trabajador(); owner.setId(3L);
        var end = LocalDate.now().plusYears(1);
        var sub = new Suscripcion(owner,"PRO",LocalDate.now(),end,true); sub.setId(44L);
        when(repository.findById(44L)).thenReturn(Optional.of(sub)); when(repository.save(sub)).thenReturn(sub);
        new SuscripcionServiceImpl(repository,usuarios,ofertas,mapper).cancelar(44L);
        assertFalse(sub.isActiva()); assertEquals(44L,sub.getId()); assertEquals(end,sub.getFechaFin());
        verify(repository).save(sub); verify(repository,never()).deleteById(any());
    }
}
