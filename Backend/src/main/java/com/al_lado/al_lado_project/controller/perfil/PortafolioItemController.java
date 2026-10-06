package com.al_lado.al_lado_project.controller.perfil;

import com.al_lado.al_lado_project.dto.perfil.PortafolioItemDTO;
import com.al_lado.al_lado_project.service.perfil.PortafolioItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portafolio-items")
@RequiredArgsConstructor
public class PortafolioItemController {

    private final PortafolioItemService service;

    @GetMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<List<PortafolioItemDTO>> obtenerPorTrabajador(@PathVariable Long trabajadorId) {
        return ResponseEntity.ok(service.obtenerPorTrabajador(trabajadorId));
    }

    @PreAuthorize("hasAnyRole('TRABAJADOR', 'ADMIN')")
    @PostMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<PortafolioItemDTO> agregar(@PathVariable Long trabajadorId, @Valid @RequestBody PortafolioItemDTO dto) {
        return new ResponseEntity<>(service.agregar(trabajadorId, dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('TRABAJADOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}