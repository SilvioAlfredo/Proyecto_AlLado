package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RF-09 / HU-12 CA-02 · AL-SEC-007 (MEJ-002): el nivel PRO solo lo concede un administrador.
 *
 * <p>Un usuario no puede auto-otorgarse PRO ni pasarse a PRO por su cuenta: el plan se
 * activa después de verificar el aporte.</p>
 */
@ExtendWith(MockitoExtension.class)
class SuscripcionConcesionProTest {

    @Mock
    private SuscripcionRepository repository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private OportunidadLaboralRepository oportunidadLaboralRepository;
    @Mock
    private SuscripcionMapper mapper;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private void autenticarComo(com.al_lado.al_lado_project.entity.usuario.Usuario usuario) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    private static Empleador empleador(Long id) {
        Empleador e = new Empleador();
        e.setId(id);
        e.setEmail("empleador" + id + "@al-lado.com");
        return e;
    }

    private SuscripcionServiceImpl service() {
        return new SuscripcionServiceImpl(repository, usuarioRepository,
                oportunidadLaboralRepository, mapper);
    }

    private SuscripcionDTO dto(String nivel) {
        UsuarioResumenDTO usuario = new UsuarioResumenDTO();
        usuario.setId(5L);
        SuscripcionDTO dto = new SuscripcionDTO();
        dto.setUsuario(usuario);
        dto.setNivel(nivel);
        dto.setFechaInicio(LocalDate.now());
        return dto;
    }

    @Test
    void crear_deberiaRechazarAutoOtorgarsePro() {
        autenticarComo(empleador(5L));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(empleador(5L)));

        assertThrows(AccessDeniedException.class, () -> service().crear(dto("PRO")));

        verify(repository, never()).save(any(Suscripcion.class));
    }

    @Test
    void crear_deberiaRechazarPromocionarDeBasicoAPro() {
        autenticarComo(empleador(5L));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(empleador(5L)));

        assertThrows(AccessDeniedException.class, () -> service().crear(dto("PRO")));

        verify(repository, never()).save(any(Suscripcion.class));
    }

    @Test
    void crear_deberiaPermitirProAlAdministrador() {
        // El admin activa el plan tras verificar el aporte del usuario.
        com.al_lado.al_lado_project.entity.usuario.Administrador admin =
                new com.al_lado.al_lado_project.entity.usuario.Administrador();
        admin.setId(1L);
        admin.setEmail("admin@al-lado.com");
        autenticarComo(admin);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(empleador(5L)));
        when(repository.existsByUsuarioId(5L)).thenReturn(false);
        Suscripcion suscripcion = new Suscripcion();
        when(mapper.toEntity(any(SuscripcionDTO.class))).thenReturn(suscripcion);
        when(repository.save(any(Suscripcion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDTO(any(Suscripcion.class))).thenAnswer(inv -> {
            SuscripcionDTO dto = new SuscripcionDTO();
            dto.setNivel(((Suscripcion) inv.getArgument(0)).getNivel());
            return dto;
        });

        SuscripcionDTO resultado = service().crear(dto("PRO"));

        assertEquals("PRO", resultado.getNivel());
    }

    @Test
    void crear_deberiaPermitirElPlanBasicoPropio() {
        autenticarComo(empleador(5L));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(empleador(5L)));
        when(repository.existsByUsuarioId(5L)).thenReturn(false);
        Suscripcion suscripcion = new Suscripcion();
        when(mapper.toEntity(any(SuscripcionDTO.class))).thenReturn(suscripcion);
        when(repository.save(any(Suscripcion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDTO(any(Suscripcion.class))).thenAnswer(inv -> {
            SuscripcionDTO dto = new SuscripcionDTO();
            dto.setNivel(((Suscripcion) inv.getArgument(0)).getNivel());
            return dto;
        });

        SuscripcionDTO resultado = service().crear(dto("BASICO"));

        assertEquals("BASICO", resultado.getNivel());
        // El alta propia no inventa un vencimiento: lo define el administrador.
        assertNull(suscripcion.getFechaFin());
    }
}
