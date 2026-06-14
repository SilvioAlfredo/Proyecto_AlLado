package com.al_lado.al_lado_project.controller;

import com.al_lado.al_lado_project.dto.TrabajadorDTO;
import com.al_lado.al_lado_project.service.TrabajadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trabajadores")
@RequiredArgsConstructor
public class TrabajadorController {

    private final TrabajadorService service;

    @GetMapping
    public ResponseEntity<List<TrabajadorDTO>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrabajadorDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TrabajadorDTO> guardar(@RequestBody TrabajadorDTO trabajadorDTO) {
        return new  ResponseEntity<>(service.guardar(trabajadorDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrabajadorDTO> actualizar(@PathVariable Long id, @RequestBody TrabajadorDTO trabajadorDTO) {
        trabajadorDTO.setId(id);
        return ResponseEntity.ok(service.guardar(trabajadorDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
