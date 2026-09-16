package com.al_lado.al_lado_project.controller.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.ReporteDTO;
import com.al_lado.al_lado_project.service.comunidad.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService service;

    @GetMapping
    public ResponseEntity<List<ReporteDTO>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReporteDTO>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(service.obtenerPorEstado(estado));
    }

    @PostMapping
    public ResponseEntity<ReporteDTO> crear(@Valid @RequestBody ReporteDTO reporteDTO) {
        return new ResponseEntity<>(service.crear(reporteDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/resolver")
    public ResponseEntity<ReporteDTO> resolver(@PathVariable Long id,
                                               @RequestParam Long moderadorId,
                                               @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(service.resolver(id, moderadorId, estado));
    }
}