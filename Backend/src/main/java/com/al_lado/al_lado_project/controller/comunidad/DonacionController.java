package com.al_lado.al_lado_project.controller.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;
import com.al_lado.al_lado_project.service.comunidad.DonacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donaciones")
@RequiredArgsConstructor
public class DonacionController {

    private final DonacionService service;

    @GetMapping
    public ResponseEntity<List<DonacionDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonacionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DonacionDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.obtenerPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<DonacionDTO> crear(@Valid @RequestBody DonacionDTO donacionDTO) {
        return new ResponseEntity<>(service.crear(donacionDTO), HttpStatus.CREATED);
    }
}