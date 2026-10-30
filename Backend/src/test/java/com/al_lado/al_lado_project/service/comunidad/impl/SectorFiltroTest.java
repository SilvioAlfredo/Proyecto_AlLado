package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;
import com.al_lado.al_lado_project.dto.comunidad.ReporteDTO;
import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.mappers.comunidad.PublicacionForoMapper;
import com.al_lado.al_lado_project.mappers.comunidad.ReporteMapper;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import com.al_lado.al_lado_project.repository.comunidad.PublicacionForoRepository;
import com.al_lado.al_lado_project.repository.comunidad.ReporteRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * GH-56 (HU-13): el foro y los reportes se pueden recorrer por sector.
 *
 * <p>Los repositorios ya tenían {@code findBySectorIgnoreCase}; ahora está expuesto
 * en el servicio y en el endpoint correspondiente.</p>
 */
@ExtendWith(MockitoExtension.class)
class SectorFiltroTest {

    @Mock
    private PublicacionForoRepository foroRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ReporteRepository reporteRepository;
    @Mock
    private AdministradorRepository administradorRepository;

    private PublicacionForoDTO publicacionDe(String sector) {
        PublicacionForo entidad = new PublicacionForo();
        entidad.setId(1L);
        entidad.setTitulo("Tema");
        entidad.setSector(sector);
        Trabajador autor = new Trabajador();
        autor.setId(1L);
        autor.setNombre("Ana");
        autor.setEmail("ana@mail.com");
        entidad.setAutor(autor);
        return new PublicacionForoMapper(new UsuarioResumenMapper()).toDTOPublic(entidad);
    }

    @Test
    void elForoSeFiltraPorSector() {
        PublicacionForoServiceImpl service = new PublicacionForoServiceImpl(
                foroRepository, usuarioRepository, new PublicacionForoMapper(new UsuarioResumenMapper()));
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        when(foroRepository.findBySectorIgnoreCase(eq("construcción"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<PublicacionForoDTO> resultado = service.obtenerPorSector("construcción", pageable);

        verify(foroRepository).findBySectorIgnoreCase("construcción", pageable);
        assertTrue(resultado.getContent().isEmpty());
    }

    @Test
    void elForoSinSectorDevuelveTodoEnElOrdenNormal() {
        PublicacionForoServiceImpl service = new PublicacionForoServiceImpl(
                foroRepository, usuarioRepository, new PublicacionForoMapper(new UsuarioResumenMapper()));
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        when(foroRepository.findAllByOrderByFechaCreacionDesc(pageable))
                .thenReturn(new PageImpl<>(List.of()));

        service.obtenerPorSector("  ", pageable);

        verify(foroRepository).findAllByOrderByFechaCreacionDesc(pageable);
    }

    @Test
    void laPublicacionPorSectorNoExponeElEmailDelAutor() {
        PublicacionForoDTO dto = publicacionDe("Albañilería");

        assertEquals("Albañilería", dto.getSector());
        assertEquals("Ana", dto.getAutor().getNombre());
        org.junit.jupiter.api.Assertions.assertNull(dto.getAutor().getEmail(),
                "El sector es público; el email del autor no (AL-SEC-006).");
    }

    @Test
    void losReportesSeFiltranPorSector() {
        ReporteServiceImpl service = new ReporteServiceImpl(reporteRepository, usuarioRepository,
                administradorRepository, new ReporteMapper(new UsuarioResumenMapper()));
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        when(reporteRepository.findBySectorIgnoreCase(eq("electricidad"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.obtenerPorSector("electricidad", pageable);

        verify(reporteRepository).findBySectorIgnoreCase("electricidad", pageable);
    }

    @Test
    void losReportesSinSectorDevuelvenTodos() {
        ReporteServiceImpl service = new ReporteServiceImpl(reporteRepository, usuarioRepository,
                administradorRepository, new ReporteMapper(new UsuarioResumenMapper()));
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        when(reporteRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        service.obtenerPorSector(null, pageable);

        verify(reporteRepository).findAll(pageable);
    }

    @Test
    void elSectorSeComparaSinDistincionDeMayusculas() {
        ReporteServiceImpl service = new ReporteServiceImpl(reporteRepository, usuarioRepository,
                administradorRepository, new ReporteMapper(new UsuarioResumenMapper()));
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        when(reporteRepository.findBySectorIgnoreCase(eq("PLOMERIA"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.obtenerPorSector("  PLOMERIA ", pageable);

        // El servicio recorta, pero no altera el valor: el repositorio se encarga de
        // ignorar mayúsculas/minúsculas.
        verify(reporteRepository).findBySectorIgnoreCase("PLOMERIA", pageable);
    }
}
