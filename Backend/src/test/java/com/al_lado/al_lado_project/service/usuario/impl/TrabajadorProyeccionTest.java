package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.usuario.TrabajadorDTO;
import com.al_lado.al_lado_project.entity.perfil.HorarioDisponibilidad;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.mappers.perfil.HorarioDisponibilidadMapper;
import com.al_lado.al_lado_project.mappers.perfil.PortafolioItemMapper;
import com.al_lado.al_lado_project.mappers.perfil.PreferenciaNotificacionMapper;
import com.al_lado.al_lado_project.mappers.trabajo.EtiquetaMapper;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionResumenMapper;
import com.al_lado.al_lado_project.mappers.usuario.TrabajadorMapper;
import com.al_lado.al_lado_project.repository.trabajo.EtiquetaRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RF-02 / RF-03 · HU-03 / HU-05 · AL-SEC-006 (MEJ-001): la proyección de las lecturas
 * depende de quién pregunta.
 *
 * <p>Un tercero (o un anónimo) recibe la proyección pública, sin email ni teléfono; el
 * titular de la cuenta y el administrador reciben el perfil completo.</p>
 */
@ExtendWith(MockitoExtension.class)
class TrabajadorProyeccionTest {

    @Mock
    private TrabajadorRepository repository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EtiquetaRepository etiquetaRepository;

    @Mock
    private EtiquetaMapper etiquetaMapper;
    @Mock
    private PostulacionResumenMapper postulacionResumenMapper;
    @Mock
    private PortafolioItemMapper portafolioItemMapper;
    @Mock
    private HorarioDisponibilidadMapper horarioDisponibilidadMapper;
    @Mock
    private PreferenciaNotificacionMapper preferenciaNotificacionMapper;

    private TrabajadorMapper mapper;
    private TrabajadorServiceImpl service;

    @BeforeEach
    void setUp() {
        mapper = new TrabajadorMapper(etiquetaMapper, postulacionResumenMapper, portafolioItemMapper,
                horarioDisponibilidadMapper, preferenciaNotificacionMapper);
        service = new TrabajadorServiceImpl(repository, usuarioRepository, etiquetaRepository,
                mapper, new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder());
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private void iniciarSesion(Usuario usuario) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    private static Trabajador trabajador(Long id) {
        Trabajador t = new Trabajador();
        t.setId(id);
        t.setNombre("Demo Trabajador");
        t.setEmail("trabajador@al-lado.com");
        t.setTelefono("+5491100000000");
        t.setSector("Barrio Norte");
        return t;
    }

    @Test
    void filtrar_delegaEtiquetaYSectorNormalizadosAlRepositorio() {
        when(repository.filtrarPorEtiquetaYSector("Electricidad", "Villa Crespo")).thenReturn(List.of());

        assertTrue(service.filtrar("  Electricidad  ", "  Villa Crespo  ").isEmpty());

        verify(repository).filtrarPorEtiquetaYSector("Electricidad", "Villa Crespo");
    }
    @Test
    void filtrar_excluyeTrabajadoresSinFranjasDeDisponibilidad() {
        Trabajador disponible = trabajador(1L);
        HorarioDisponibilidad franja = new HorarioDisponibilidad(
                "LUNES", LocalTime.of(9, 0), LocalTime.of(17, 0), disponible);
        disponible.setHorarios(new HashSet<>(List.of(franja)));
        Trabajador sinDisponibilidad = trabajador(2L);
        sinDisponibilidad.setHorarios(new HashSet<>());

        when(repository.filtrarPorEtiquetaYSector(null, null))
                .thenReturn(List.of(disponible, sinDisponibilidad));

        List<TrabajadorDTO> resultado = service.filtrar(null, null);

        assertEquals(List.of(1L), resultado.stream().map(TrabajadorDTO::getId).toList());
    }
    @Test
    void toDTOPublic_noDebeExponerEmailNiTelefono() {
        TrabajadorDTO dto = mapper.toDTOPublic(trabajador(1L));

        assertNull(dto.getEmail(), "La proyección pública no debe exponer el email.");
        assertNull(dto.getTelefono(), "La proyección pública no debe exponer el teléfono.");
        assertTrue(dto.getPostulaciones() == null || dto.getPostulaciones().isEmpty());
        assertTrue(dto.getPortafolioItems() == null || dto.getPortafolioItems().isEmpty());
        assertTrue(dto.getHorarios() == null || dto.getHorarios().isEmpty());
        assertNull(dto.getPreferenciaNotificacion());
    }

    @Test
    void toDTOPublic_debeConservarLosDatosDelDirectorio() {
        TrabajadorDTO dto = mapper.toDTOPublic(trabajador(1L));

        assertEquals(1L, dto.getId());
        assertEquals("Demo Trabajador", dto.getNombre());
        assertEquals("Barrio Norte", dto.getSector());
    }

    @Test
    void obtenerPorId_debeDevolverLaProyeccionPublicaATerceros() {
        Empleador empleador = new Empleador();
        empleador.setId(9L);
        iniciarSesion(empleador);
        when(repository.findById(1L)).thenReturn(Optional.of(trabajador(1L)));

        TrabajadorDTO dto = service.obtenerPorId(1L);

        assertNull(dto.getEmail());
        assertNull(dto.getTelefono());
    }

    @Test
    void obtenerPorId_debeDevolverElPerfilCompletoAlTitular() {
        Trabajador titular = trabajador(1L);
        iniciarSesion(titular);
        when(repository.findById(1L)).thenReturn(Optional.of(titular));

        TrabajadorDTO dto = service.obtenerPorId(1L);

        assertEquals("trabajador@al-lado.com", dto.getEmail());
        assertEquals("+5491100000000", dto.getTelefono());
    }

    @Test
    void obtenerPorId_debeDevolverElPerfilCompletoAlAdministrador() {
        com.al_lado.al_lado_project.entity.usuario.Administrador admin =
                new com.al_lado.al_lado_project.entity.usuario.Administrador();
        admin.setId(1L);
        admin.setEmail("admin@al-lado.com");
        iniciarSesion(admin);
        when(repository.findById(3L)).thenReturn(Optional.of(trabajador(3L)));

        TrabajadorDTO dto = service.obtenerPorId(3L);

        assertNotNull(dto.getEmail());
        assertEquals("trabajador@al-lado.com", dto.getEmail());
    }
}
