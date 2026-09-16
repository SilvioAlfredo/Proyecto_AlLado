package com.al_lado.al_lado_project.controller.usuario;

import com.al_lado.al_lado_project.dto.usuario.AdministradorDTO;
import com.al_lado.al_lado_project.service.usuario.AdministradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/administradores")
@RequiredArgsConstructor
public class AdministradorController {

    private final AdministradorService service;

    @GetMapping
    public ResponseEntity<List<AdministradorDTO>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<AdministradorDTO> guardar(@Valid @RequestBody AdministradorDTO administradorDTO) {
        return new ResponseEntity<>(service.guardar(administradorDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AdministradorDTO administradorDTO) {
        administradorDTO.setId(id);
        return ResponseEntity.ok(service.guardar(administradorDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}