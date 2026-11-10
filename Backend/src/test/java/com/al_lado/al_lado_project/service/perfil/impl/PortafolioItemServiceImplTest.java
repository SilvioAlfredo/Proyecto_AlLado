package com.al_lado.al_lado_project.service.perfil.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.al_lado.al_lado_project.dto.perfil.PortafolioItemDTO;
import com.al_lado.al_lado_project.entity.perfil.PortafolioItem;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.mappers.perfil.PortafolioItemMapper;
import com.al_lado.al_lado_project.repository.perfil.PortafolioItemRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.service.perfil.PortafolioItemService;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;

@ExtendWith(MockitoExtension.class)
class PortafolioItemServiceImplTest {

    @Mock private PortafolioItemRepository portafolioRepository;
    @Mock private TrabajadorRepository trabajadorRepository;
    @Mock private SuscripcionService suscripcionService;
    private final PortafolioItemMapper mapper = new PortafolioItemMapper();
    private PortafolioItemService service;

    private Trabajador trabajador;
    private PortafolioItem item;

    @BeforeEach
    void setUp() {
        service = new PortafolioItemServiceImpl(
                portafolioRepository, trabajadorRepository, mapper, suscripcionService);

        // Las operaciones exigen titularidad: autenticar como el trabajador 1
        SecurityContextHolder.clearContext();
        Trabajador sesion = new Trabajador();
        sesion.setId(1L);
        sesion.setEmail("trabajador@test.com");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(sesion, null, sesion.getAuthorities()));

        trabajador = new Trabajador();
        trabajador.setId(1L);
        trabajador.setNombre("Trabajador PRO");

        item = new PortafolioItem();
        item.setId(1L);
        item.setTrabajador(trabajador);
        item.setTitulo("Proyecto de ejemplo");
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void agregar_deberiaCrearItemCuandoTrabajadorTienePRO() {
        // Dado
        when(suscripcionService.esProVigente(1L)).thenReturn(true);
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(portafolioRepository.save(any(PortafolioItem.class))).thenReturn(item);

        // Cuando
        PortafolioItemDTO dto = new PortafolioItemDTO();
        dto.setTitulo("Proyecto de ejemplo");
        dto.setDescripcion("Descripción del proyecto");
        PortafolioItemDTO resultado = service.agregar(1L, dto);

        // Entonces
        assertNotNull(resultado);
    }

    @Test
    void agregar_deberiaRechazarCuandoTrabajadorNoTienePRO() {
        // Dado
        when(suscripcionService.esProVigente(1L)).thenReturn(false);

        // Cuando
        PortafolioItemDTO dto = new PortafolioItemDTO();
        dto.setTitulo("Proyecto");

        // Entonces
        assertThrows(BusinessRuleException.class, () -> service.agregar(1L, dto));
    }

    @Test
    void eliminar_deberiaEliminarCuandoExiste() {
        // Dado
        when(portafolioRepository.findById(1L)).thenReturn(Optional.of(item));

        // Cuando
        service.eliminar(1L);

        // Entonces
        verify(portafolioRepository, times(1)).delete(item);
    }
}
