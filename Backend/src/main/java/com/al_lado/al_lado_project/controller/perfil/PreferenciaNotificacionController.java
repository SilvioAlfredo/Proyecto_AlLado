package com.al_lado.al_lado_project.controller.perfil;

import com.al_lado.al_lado_project.dto.perfil.PreferenciaNotificacionDTO;
import com.al_lado.al_lado_project.service.perfil.PreferenciaNotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/preferencias")
@RequiredArgsConstructor
public class PreferenciaNotificacionController {

    private final PreferenciaNotificacionService service;

    @GetMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<PreferenciaNotificacionDTO> obtenerPorTrabajador(@PathVariable Long trabajadorId) {
        return ResponseEntity.ok(service.obtenerPorTrabajador(trabajadorId));
    }

    @PreAuthorize("hasAnyRole('TRABAJADOR', 'ADMIN')")
    @PutMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<PreferenciaNotificacionDTO> guardar(@PathVariable Long trabajadorId, @Valid @RequestBody PreferenciaNotificacionDTO dto) {
        return ResponseEntity.ok(service.guardar(trabajadorId, dto));
    }
}