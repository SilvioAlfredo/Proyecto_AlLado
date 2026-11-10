package com.al_lado.al_lado_project.service.trabajo.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.al_lado.al_lado_project.dto.trabajo.EtiquetaDTO;
import com.al_lado.al_lado_project.entity.trabajo.Etiqueta;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.mappers.trabajo.EtiquetaMapper;
import com.al_lado.al_lado_project.repository.trabajo.EtiquetaRepository;
import com.al_lado.al_lado_project.service.trabajo.EtiquetaService;
import com.al_lado.al_lado_project.service.trabajo.impl.EtiquetaServiceImpl;

@ExtendWith(MockitoExtension.class)
class EtiquetaServiceImplTest {

    @Mock private EtiquetaRepository etiquetaRepository;
    private final EtiquetaMapper mapper = new EtiquetaMapper();
    private EtiquetaService service;

    private Etiqueta etiqueta;

    @BeforeEach
    void setUp() {
        service = new EtiquetaServiceImpl(etiquetaRepository, mapper);

        etiqueta = new Etiqueta();
        etiqueta.setId(1L);
        etiqueta.setNombre("Electricidad");
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void obtenerOCrearPorNombre_deberiaCrearEtiquetaCuandoNoExiste() {
        // Dado
        when(etiquetaRepository.findByNombreIgnoreCase("electricidad")).thenReturn(java.util.Optional.empty());
        when(etiquetaRepository.save(any(Etiqueta.class))).thenAnswer(invocation -> {
            Etiqueta e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        // Cuando
        EtiquetaDTO resultado = service.obtenerOCrearPorNombre("electricidad");

        // Entonces
        assertNotNull(resultado);
        assertEquals("electricidad", resultado.getNombre());
        verify(etiquetaRepository, times(1)).save(any(Etiqueta.class));
    }

    @Test
    void obtenerOCrearPorNombre_deberiaRetornarExistenteCuandoYaExiste() {
        // Dado
        when(etiquetaRepository.findByNombreIgnoreCase("electricidad")).thenReturn(java.util.Optional.of(etiqueta));

        // Cuando
        EtiquetaDTO resultado = service.obtenerOCrearPorNombre("electricidad");

        // Entonces
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(etiquetaRepository, never()).save(any(Etiqueta.class));
    }

    @Test
    void eliminar_deberiaEliminarCuandoEtiquetaNoEstaEnUso() {
        // Dado: la etiqueta existe y sus colecciones están vacías (no está en uso).
        when(etiquetaRepository.findById(1L)).thenReturn(java.util.Optional.of(etiqueta));

        // Cuando
        service.eliminar(1L);

        // Entonces
        verify(etiquetaRepository, times(1)).delete(etiqueta);
    }

    @Test
    void eliminar_deberiaRechazarCuandoEtiquetaEstaEnUso() {
        // Dado: la etiqueta está asociada al menos a un trabajador.
        etiqueta.getTrabajadores().add(new Trabajador());
        when(etiquetaRepository.findById(1L)).thenReturn(java.util.Optional.of(etiqueta));

        // Cuando
        assertThrows(BusinessRuleException.class, () -> service.eliminar(1L));

        // Entonces
        verify(etiquetaRepository, never()).delete(any(Etiqueta.class));
    }
}
