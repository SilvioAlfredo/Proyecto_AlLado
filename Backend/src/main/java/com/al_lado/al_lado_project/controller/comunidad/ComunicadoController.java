package com.al_lado.al_lado_project.controller.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.service.comunidad.ComunicadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comunicados")
@RequiredArgsConstructor
public class ComunicadoController {

    private final ComunicadoService service;

    @GetMapping
    public ResponseEntity<List<ComunicadoDTO>> obtenerTodos(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.obtenerTodos(PageRequest.of(page, size)).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComunicadoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/alcance/{alcance}")
    public ResponseEntity<List<ComunicadoDTO>> obtenerPorAlcance(
            @PathVariable String alcance,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.obtenerPorAlcance(alcance, PageRequest.of(page, size)).getContent());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ComunicadoDTO> crear(@Valid @RequestBody ComunicadoDTO comunicadoDTO) {
        return new ResponseEntity<>(service.crear(comunicadoDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}