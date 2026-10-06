package com.al_lado.al_lado_project.controller.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;
import com.al_lado.al_lado_project.service.comunidad.ValoracionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/valoraciones")
@RequiredArgsConstructor
public class ValoracionController {

    private final ValoracionService service;

    @GetMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<List<ValoracionDTO>> obtenerPorTrabajador(
            @PathVariable Long trabajadorId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.obtenerPorTrabajador(trabajadorId, PageRequest.of(page, size)).getContent());
    }

    @GetMapping("/trabajador/{trabajadorId}/promedio")
    public ResponseEntity<Double> obtenerPromedioPorTrabajador(@PathVariable Long trabajadorId) {
        return ResponseEntity.ok(service.obtenerPromedioPorTrabajador(trabajadorId));
    }

    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<ValoracionDTO>> obtenerPorAutor(
            @PathVariable Long autorId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.obtenerPorAutor(autorId, PageRequest.of(page, size)).getContent());
    }

    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ValoracionDTO> crear(@Valid @RequestBody ValoracionDTO valoracionDTO) {
        return new ResponseEntity<>(service.crear(valoracionDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}