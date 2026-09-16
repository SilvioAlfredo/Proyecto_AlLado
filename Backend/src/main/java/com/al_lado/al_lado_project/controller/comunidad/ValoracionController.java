package com.al_lado.al_lado_project.controller.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;
import com.al_lado.al_lado_project.service.comunidad.ValoracionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/valoraciones")
@RequiredArgsConstructor
public class ValoracionController {

    private final ValoracionService service;

    @GetMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<List<ValoracionDTO>> obtenerPorTrabajador(@PathVariable Long trabajadorId) {
        return ResponseEntity.ok(service.obtenerPorTrabajador(trabajadorId));
    }

    @GetMapping("/trabajador/{trabajadorId}/promedio")
    public ResponseEntity<Double> obtenerPromedioPorTrabajador(@PathVariable Long trabajadorId) {
        return ResponseEntity.ok(service.obtenerPromedioPorTrabajador(trabajadorId));
    }

    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<ValoracionDTO>> obtenerPorAutor(@PathVariable Long autorId) {
        return ResponseEntity.ok(service.obtenerPorAutor(autorId));
    }

    @PostMapping
    public ResponseEntity<ValoracionDTO> crear(@Valid @RequestBody ValoracionDTO valoracionDTO) {
        return new ResponseEntity<>(service.crear(valoracionDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}