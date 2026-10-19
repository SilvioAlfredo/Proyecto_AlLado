package com.al_lado.al_lado_project.controller.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.PerfilEmpresarialDTO;
import com.al_lado.al_lado_project.service.comunidad.PerfilEmpresarialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfiles-empresariales")
@RequiredArgsConstructor
public class PerfilEmpresarialController {

    private final PerfilEmpresarialService service;

    @GetMapping("/empleador/{empleadorId}")
    public ResponseEntity<PerfilEmpresarialDTO> obtenerPorEmpleador(@PathVariable Long empleadorId) {
        return ResponseEntity.ok(service.obtenerPorEmpleador(empleadorId));
    }

    @PutMapping("/empleador/{empleadorId}")
    public ResponseEntity<PerfilEmpresarialDTO> guardar(@PathVariable Long empleadorId, @Valid @RequestBody PerfilEmpresarialDTO dto) {
        return ResponseEntity.ok(service.guardar(empleadorId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/verificar")
    public ResponseEntity<PerfilEmpresarialDTO> verificar(@PathVariable Long id, @RequestParam(required = false) Long supervisorId) {
        return ResponseEntity.ok(service.verificar(id, supervisorId));
    }
}