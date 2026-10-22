package com.al_lado.al_lado_project.controller.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.service.trabajo.PostulacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/postulaciones")
@RequiredArgsConstructor
public class PostulacionController {

    private final PostulacionService service;

    @GetMapping
    public ResponseEntity<List<PostulacionDTO>> listarPostulaciones() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<PostulacionDTO> obtenerPostulacion(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PreAuthorize("hasRole('TRABAJADOR')")
    @PostMapping
    public ResponseEntity<PostulacionDTO> crearPostulacion(@Valid @RequestBody PostulacionDTO postulacionDTO) {
        return new ResponseEntity<>(service.crearPostulacion(postulacionDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @PatchMapping("/{id:[0-9]+}/seleccionar")
    public ResponseEntity<PostulacionDTO> seleccionarPostulacion(@PathVariable Long id) {
        return ResponseEntity.ok(service.seleccionarPostulacion(id));
    }

    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @PatchMapping("/{id:[0-9]+}/rechazar")
    public ResponseEntity<PostulacionDTO> rechazarPostulacion(@PathVariable Long id) {
        return ResponseEntity.ok(service.rechazarPostulacion(id));
    }

    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @PatchMapping("/{id:[0-9]+}/completar")
    public ResponseEntity<PostulacionDTO> completarPostulacion(@PathVariable Long id) {
        return ResponseEntity.ok(service.completarPostulacion(id));
    }

    @PreAuthorize("hasRole('TRABAJADOR')")
    @PatchMapping("/{id:[0-9]+}/retirar")
    public ResponseEntity<PostulacionDTO> retirarPostulacion(@PathVariable Long id) {
        return ResponseEntity.ok(service.retirarPostulacion(id));
    }
}