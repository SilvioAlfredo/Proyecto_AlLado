package com.al_lado.al_lado_project.controller.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;
import com.al_lado.al_lado_project.service.comunidad.PublicacionForoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foro")
@RequiredArgsConstructor
public class PublicacionForoController {

    private final PublicacionForoService service;

    @GetMapping
    public ResponseEntity<List<PublicacionForoDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicacionForoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<PublicacionForoDTO>> obtenerPorAutor(@PathVariable Long autorId) {
        return ResponseEntity.ok(service.obtenerPorAutor(autorId));
    }

    @PostMapping
    public ResponseEntity<PublicacionForoDTO> crear(@Valid @RequestBody PublicacionForoDTO publicacionDTO) {
        return new ResponseEntity<>(service.crear(publicacionDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}