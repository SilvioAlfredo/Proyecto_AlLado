package com.al_lado.al_lado_project.controller.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.service.trabajo.OportunidadLaboralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/oportunidades")
@RequiredArgsConstructor
public class OportunidadLaboralController {

    private final OportunidadLaboralService service;

    @GetMapping
    public ResponseEntity<List<OportunidadLaboralDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OportunidadLaboralDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<OportunidadLaboralDTO> guardar(@Valid @RequestBody OportunidadLaboralDTO oportunidadDTO) {
        return new ResponseEntity<>(service.guardar(oportunidadDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OportunidadLaboralDTO> actualizar(@PathVariable Long id, @Valid @RequestBody OportunidadLaboralDTO oportunidadDTO) {
        oportunidadDTO.setId(id);
        return ResponseEntity.ok(service.guardar(oportunidadDTO));
    }

    @PutMapping("/cerrar/{id}")
    public ResponseEntity<OportunidadLaboralDTO> cerrarOportunidad(@PathVariable Long id) {
        return ResponseEntity.ok(service.cerrarOportunidad(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}