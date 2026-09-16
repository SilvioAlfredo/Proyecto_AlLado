package com.al_lado.al_lado_project.controller.perfil;

import com.al_lado.al_lado_project.dto.perfil.HorarioDisponibilidadDTO;
import com.al_lado.al_lado_project.service.perfil.HorarioDisponibilidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horarios")
@RequiredArgsConstructor
public class HorarioDisponibilidadController {

    private final HorarioDisponibilidadService service;

    @GetMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<List<HorarioDisponibilidadDTO>> obtenerPorTrabajador(@PathVariable Long trabajadorId) {
        return ResponseEntity.ok(service.obtenerPorTrabajador(trabajadorId));
    }

    @PostMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<HorarioDisponibilidadDTO> agregar(@PathVariable Long trabajadorId, @Valid @RequestBody HorarioDisponibilidadDTO dto) {
        return new ResponseEntity<>(service.agregar(trabajadorId, dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}