package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.dto.comunidad.PublicidadDTO;
import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;
import com.al_lado.al_lado_project.dto.perfil.HorarioDisponibilidadDTO;
import com.al_lado.al_lado_project.dto.trabajo.EtiquetaDTO;
import com.al_lado.al_lado_project.dto.usuario.EmpleadorDTO;
import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;
import com.al_lado.al_lado_project.dto.usuario.TrabajadorDTO;
import com.al_lado.al_lado_project.mappers.comunidad.ComunicadoMapper;
import com.al_lado.al_lado_project.mappers.comunidad.PerfilEmpresarialMapper;
import com.al_lado.al_lado_project.mappers.comunidad.PublicidadMapper;
import com.al_lado.al_lado_project.mappers.comunidad.ValoracionMapper;
import com.al_lado.al_lado_project.mappers.perfil.HorarioDisponibilidadMapper;
import com.al_lado.al_lado_project.mappers.perfil.PortafolioItemMapper;
import com.al_lado.al_lado_project.mappers.perfil.PreferenciaNotificacionMapper;
import com.al_lado.al_lado_project.mappers.trabajo.EtiquetaMapper;
import com.al_lado.al_lado_project.mappers.trabajo.OportunidadLaboralResumenMapper;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionResumenMapper;
import com.al_lado.al_lado_project.mappers.usuario.EmpleadorMapper;
import com.al_lado.al_lado_project.mappers.usuario.SuscripcionMapper;
import com.al_lado.al_lado_project.mappers.usuario.TrabajadorMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * MEJ-003 / AL-SEC-008: el ID que manda el cliente nunca se copia en el alta.
 *
 * <p>Si el mapper copiara el ID, una petición con un ID ajeno apuntaría el alta a un
 * registro existente y lo sobrescribiría.</p>
 */
class AltasSinIdDelClienteTest {

    private final UsuarioResumenMapper resumen = new UsuarioResumenMapper();

    @Test
    void trabajadorNoHeredaElIdDelCliente() {
        TrabajadorMapper mapper = new TrabajadorMapper(new EtiquetaMapper(),
                new PostulacionResumenMapper(resumen), new PortafolioItemMapper(),
                new HorarioDisponibilidadMapper(), new PreferenciaNotificacionMapper());
        TrabajadorDTO dto = new TrabajadorDTO();
        dto.setId(99L);

        assertNull(mapper.toEntity(dto).getId());
    }

    @Test
    void empleadorNoHeredaElIdDelCliente() {
        EmpleadorMapper mapper = new EmpleadorMapper(new PerfilEmpresarialMapper(resumen),
                new OportunidadLaboralResumenMapper());
        EmpleadorDTO dto = new EmpleadorDTO();
        dto.setId(99L);

        assertNull(mapper.toEntity(dto).getId());
    }

    @Test
    void suscripcionNoHeredaElIdDelCliente() {
        SuscripcionDTO dto = new SuscripcionDTO();
        dto.setId(99L);

        assertNull(new SuscripcionMapper(resumen, Clock.systemDefaultZone()).toEntity(dto).getId());
    }

    @Test
    void valoracionNoHeredaElIdDelCliente() {
        ValoracionDTO dto = new ValoracionDTO();
        dto.setId(99L);
        dto.setPuntuacion(5);

        assertNull(new ValoracionMapper(resumen).toEntity(dto).getId());
    }

    @Test
    void comunicadoNoHeredaElIdDelCliente() {
        ComunicadoDTO dto = new ComunicadoDTO();
        dto.setId(99L);

        assertNull(new ComunicadoMapper(resumen).toEntity(dto).getId());
    }

    @Test
    void publicidadNoHeredaElIdDelCliente() {
        PublicidadDTO dto = new PublicidadDTO();
        dto.setId(99L);

        assertNull(new PublicidadMapper(resumen).toEntity(dto).getId());
    }

    @Test
    void horarioNoHeredaElIdDelCliente() {
        HorarioDisponibilidadDTO dto = new HorarioDisponibilidadDTO();
        dto.setId(99L);

        assertNull(new HorarioDisponibilidadMapper().toEntity(dto).getId());
    }

    @Test
    void etiquetaNoHeredaElIdDelCliente() {
        EtiquetaDTO dto = new EtiquetaDTO();
        dto.setId(99L);

        assertNull(new EtiquetaMapper().toEntity(dto).getId());
    }

    @Test
    void elAltaConservaElNombreYNoPierdeLosDemasCampos() {
        TrabajadorMapper mapper = new TrabajadorMapper(new EtiquetaMapper(),
                new PostulacionResumenMapper(resumen), new PortafolioItemMapper(),
                new HorarioDisponibilidadMapper(), new PreferenciaNotificacionMapper());
        TrabajadorDTO dto = new TrabajadorDTO();
        dto.setId(99L);
        dto.setNombre("Ana");
        dto.setEmail("ana@mail.com");

        var entidad = mapper.toEntity(dto);

        assertNull(entidad.getId());
        assertEquals("Ana", entidad.getNombre());
        assertEquals("ana@mail.com", entidad.getEmail());
    }
}
