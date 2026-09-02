package com.al_lado.al_lado_project.controller;

import com.al_lado.al_lado_project.dto.EmpleadorDTO;
import com.al_lado.al_lado_project.service.EmpleadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleadores")
@RequiredArgsConstructor
public class EmpleadorController {

    private final EmpleadorService service;

    @GetMapping
    public ResponseEntity<List<EmpleadorDTO>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadorDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<EmpleadorDTO> guardar(@RequestBody EmpleadorDTO empleadorDTO) {
        return new ResponseEntity<>(service.guardar(empleadorDTO),  HttpStatus.CREATED) ;
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadorDTO> actualizar(@PathVariable Long id, @RequestBody EmpleadorDTO empleadorDTO) {
        empleadorDTO.setId(id);
        return ResponseEntity.ok(service.guardar(empleadorDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
