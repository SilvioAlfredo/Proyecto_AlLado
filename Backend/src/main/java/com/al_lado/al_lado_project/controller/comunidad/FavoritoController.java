package com.al_lado.al_lado_project.controller.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.FavoritoDTO;
import com.al_lado.al_lado_project.service.comunidad.FavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService service;

    @GetMapping("/empleador/{empleadorId}")
    public ResponseEntity<List<FavoritoDTO>> obtenerPorEmpleador(@PathVariable Long empleadorId) {
        return ResponseEntity.ok(service.obtenerPorEmpleador(empleadorId));
    }

    @PostMapping("/empleador/{empleadorId}/trabajador/{trabajadorId}")
    public ResponseEntity<FavoritoDTO> agregar(@PathVariable Long empleadorId, @PathVariable Long trabajadorId) {
        return new ResponseEntity<>(service.agregar(empleadorId, trabajadorId), HttpStatus.CREATED);
    }

    @DeleteMapping("/empleador/{empleadorId}/trabajador/{trabajadorId}")
    public ResponseEntity<Void> quitar(@PathVariable Long empleadorId, @PathVariable Long trabajadorId) {
        service.quitar(empleadorId, trabajadorId);
        return ResponseEntity.noContent().build();
    }
}