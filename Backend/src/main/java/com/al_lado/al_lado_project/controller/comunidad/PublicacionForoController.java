package com.al_lado.al_lado_project.controller.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;
import com.al_lado.al_lado_project.service.comunidad.PublicacionForoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foro")
@RequiredArgsConstructor
public class PublicacionForoController {

    private final PublicacionForoService service;

    @GetMapping
    public ResponseEntity<List<PublicacionForoDTO>> obtenerTodas(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.obtenerTodas(PageRequest.of(page, size)).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicacionForoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<PublicacionForoDTO>> obtenerPorAutor(
            @PathVariable Long autorId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.obtenerPorAutor(autorId, PageRequest.of(page, size)).getContent());
    }

    @PostMapping
    public ResponseEntity<PublicacionForoDTO> crear(@Valid @RequestBody PublicacionForoDTO publicacionDTO) {
        return new ResponseEntity<>(service.crear(publicacionDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('TRABAJADOR', 'EMPLEADOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}