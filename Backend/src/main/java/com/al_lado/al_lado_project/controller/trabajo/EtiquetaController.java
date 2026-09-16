package com.al_lado.al_lado_project.controller.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.EtiquetaDTO;
import com.al_lado.al_lado_project.service.trabajo.EtiquetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/etiquetas")
@RequiredArgsConstructor
public class EtiquetaController {

    private final EtiquetaService service;

    @GetMapping
    public ResponseEntity<List<EtiquetaDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EtiquetaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<EtiquetaDTO> obtenerOCrear(@Valid @RequestBody EtiquetaDTO etiquetaDTO) {
        return new ResponseEntity<>(service.obtenerOCrearPorNombre(etiquetaDTO.getNombre()), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}