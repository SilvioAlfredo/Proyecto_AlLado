package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.ContactoEmpleadorDTO;
import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.trabajo.EtiquetaMapper;
import com.al_lado.al_lado_project.mappers.trabajo.OportunidadLaboralMapper;
import com.al_lado.al_lado_project.mappers.trabajo.OportunidadLaboralResumenMapper;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionResumenMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import com.al_lado.al_lado_project.repository.trabajo.EtiquetaRepository;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * AL-SEC-006 (MEJ-001): la oferta se proyecta según quién pregunta y el contacto
 * sólo lo entrega el endpoint de contacto a quien tiene relación con la oferta.
 */
@ExtendWith(MockitoExtension.class)
class OportunidadProyeccionTest {

    @Mock
    private OportunidadLaboralRepository repository;
    @Mock
    private EmpleadorRepository empleadorRepository;
    @Mock
    private EtiquetaRepository etiquetaRepository;
    @Mock
    private TrabajadorRepository trabajadorRepository;
    @Mock
    private SuscripcionService suscripcionService;

    private OportunidadLaboralMapper mapper;
    private OportunidadLaboralServiceImpl service;

    @BeforeEach
    void setUp() {
        UsuarioResumenMapper resumen = new UsuarioResumenMapper();
        mapper = new OportunidadLaboralMapper(new EtiquetaMapper(),
                new PostulacionResumenMapper(resumen), resumen);
        service = new OportunidadLaboralServiceImpl(repository, empleadorRepository,
                etiquetaRepository, mapper, trabajadorRepository, suscripcionService);
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private void iniciarSesion(Usuario usuario) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    private static Empleador empleador(Long id) {
        Empleador e = new Empleador();
        e.setId(id);
        e.setNombre("Empresa " + id);
        e.setEmail("empleador" + id + "@mail.com");
        e.setTelefono("+549110000000" + id);
        return e;
    }

    private static Trabajador trabajador(Long id) {
        Trabajador t = new Trabajador();
        t.setId(id);
        t.setNombre("Trabajador " + id);
        t.setEmail("trabajador" + id + "@mail.com");
        return t;
    }

    /** Oferta con una postulación, para ver el efecto del detalle de postulantes. */
    private static OportunidadLaboral ofertaConPostulante(Long id, Empleador autor, Trabajador postulante) {
        OportunidadLaboral oferta = new OportunidadLaboral();
        oferta.setId(id);
        oferta.setTitulo("Oferta " + id);
        oferta.setContenido("Contenido");
        oferta.setAutor(autor);
        Postulacion postulacion = new Postulacion("Postulación", "Me postulo", postulante, oferta, 100.0);
        postulacion.setAutor(postulante);
        oferta.getPostulaciones().add(postulacion);
        return oferta;
    }

    @Test
    void laProyeccionPublicaNoExponeContactoNiPostulantes() {
        Empleador autor = empleador(5L);
        OportunidadLaboral oferta = ofertaConPostulante(7L, autor, trabajador(1L));

        OportunidadLaboralDTO dto = mapper.toDTOPublic(oferta);

        assertNull(dto.getAutor().getEmail(), "El autor público no expone el email.");
        assertTrue(dto.getPostulaciones() == null || dto.getPostulaciones().isEmpty(),
                "La proyección pública no lista los postulantes.");
        assertEquals(1, dto.getPostulacionesCount(),
                "El conteo de postulaciones sí se informa.");
        assertEquals("Oferta 7", dto.getTitulo());
    }

    @Test
    void unTerceroNoVeElDetalleDePostulantes() {
        Empleador autor = empleador(5L);
        when(repository.findById(7L)).thenReturn(
                Optional.of(ofertaConPostulante(7L, autor, trabajador(1L))));
        iniciarSesion(empleador(9L));

        OportunidadLaboralDTO dto = service.obtenerPorId(7L);

        assertNull(dto.getAutor().getEmail());
        assertTrue(dto.getPostulaciones().isEmpty());
        assertEquals(1, dto.getPostulacionesCount());
    }

    @Test
    void elTitularVeElDetalleCompleto() {
        Empleador autor = empleador(5L);
        when(repository.findById(7L)).thenReturn(
                Optional.of(ofertaConPostulante(7L, autor, trabajador(1L))));
        iniciarSesion(autor);

        OportunidadLaboralDTO dto = service.obtenerPorId(7L);

        assertEquals("empleador5@mail.com", dto.getAutor().getEmail());
        assertEquals(1, dto.getPostulaciones().size());
        assertEquals(1, dto.getPostulacionesCount());
    }

    @Test
    void unaOfertaInexistenteSigueSiendo404ParaTodos() {
        when(repository.findById(404L)).thenReturn(Optional.empty());
        iniciarSesion(empleador(5L));

        assertThrows(ResourceNotFoundException.class, () -> service.obtenerPorId(404L));
    }

    @Test
    void elTitularRecibeSuPropioContacto() {
        Empleador autor = empleador(5L);
        when(repository.findById(7L)).thenReturn(
                Optional.of(ofertaConPostulante(7L, autor, trabajador(1L))));
        iniciarSesion(autor);

        ContactoEmpleadorDTO contacto = service.obtenerContacto(7L);

        assertEquals("empleador5@mail.com", contacto.getEmail());
        assertEquals("+5491100000005", contacto.getTelefono());
        assertEquals("Empresa 5", contacto.getNombre());
    }

    @Test
    void quienSePostuloRecibeElContacto() {
        Empleador autor = empleador(5L);
        Trabajador postulante = trabajador(1L);
        when(repository.findById(7L)).thenReturn(
                Optional.of(ofertaConPostulante(7L, autor, postulante)));
        iniciarSesion(postulante);

        ContactoEmpleadorDTO contacto = service.obtenerContacto(7L);

        assertNotNull(contacto.getEmail());
        assertEquals("empleador5@mail.com", contacto.getEmail());
    }

    @Test
    void unAdministradorRecibeElContacto() {
        Empleador autor = empleador(5L);
        when(repository.findById(7L)).thenReturn(
                Optional.of(ofertaConPostulante(7L, autor, trabajador(1L))));
        Administrador admin = new Administrador();
        admin.setId(9L);
        admin.setEmail("admin@al-lado.com");
        iniciarSesion(admin);

        ContactoEmpleadorDTO contacto = service.obtenerContacto(7L);

        assertEquals("empleador5@mail.com", contacto.getEmail());
    }

    @Test
    void unTerceroNoRecibeElContacto() {
        Empleador autor = empleador(5L);
        when(repository.findById(7L)).thenReturn(
                Optional.of(ofertaConPostulante(7L, autor, trabajador(1L))));
        iniciarSesion(trabajador(2L));

        assertThrows(AccessDeniedException.class, () -> service.obtenerContacto(7L));
    }
}
