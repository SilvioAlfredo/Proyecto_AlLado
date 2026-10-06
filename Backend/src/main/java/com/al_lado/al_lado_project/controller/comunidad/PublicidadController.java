package com.al_lado.al_lado_project.controller.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.PublicidadDTO;
import com.al_lado.al_lado_project.service.comunidad.PublicidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publicidades")
@RequiredArgsConstructor
public class PublicidadController {

    private final PublicidadService service;

    @GetMapping
    public ResponseEntity<List<PublicidadDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicidadDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/anunciante/{anuncianteId}")
    public ResponseEntity<List<PublicidadDTO>> obtenerPorAnunciante(@PathVariable Long anuncianteId) {
        return ResponseEntity.ok(service.obtenerPorAnunciante(anuncianteId));
    }

    @GetMapping("/vigentes")
    public ResponseEntity<List<PublicidadDTO>> obtenerVigentes() {
        return ResponseEntity.ok(service.obtenerVigentes());
    }

    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<PublicidadDTO> guardar(@Valid @RequestBody PublicidadDTO publicidadDTO) {
        return new ResponseEntity<>(service.guardar(publicidadDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PublicidadDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PublicidadDTO publicidadDTO) {
        publicidadDTO.setId(id);
        return ResponseEntity.ok(service.guardar(publicidadDTO));
    }

    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}