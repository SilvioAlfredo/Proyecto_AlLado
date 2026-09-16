package com.al_lado.al_lado_project.controller.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.service.trabajo.PostulacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/postulaciones")
@RequiredArgsConstructor
public class PostulacionController {

    private final PostulacionService service;

    @GetMapping
    public ResponseEntity<List<PostulacionDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostulacionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<PostulacionDTO> crearPostulacion(@Valid @RequestBody PostulacionDTO postulacionDTO) {
        return new ResponseEntity<>(service.crearPostulacion(postulacionDTO), HttpStatus.CREATED);
    }

    @PutMapping("/seleccionar/{id}")
    public ResponseEntity<PostulacionDTO> seleccionarPostulacion(@PathVariable Long id) {
        return ResponseEntity.ok(service.seleccionarPostulacion(id));
    }

    @PutMapping("/rechazar/{id}")
    public ResponseEntity<PostulacionDTO> rechazarPostulacion(@PathVariable Long id) {
        return ResponseEntity.ok(service.rechazarPostulacion(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}