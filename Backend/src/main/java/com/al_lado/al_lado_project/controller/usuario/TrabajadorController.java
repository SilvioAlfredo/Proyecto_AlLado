package com.al_lado.al_lado_project.controller.usuario;

import com.al_lado.al_lado_project.dto.usuario.EstadisticasTrabajadorDTO;
import com.al_lado.al_lado_project.dto.usuario.TrabajadorDTO;
import com.al_lado.al_lado_project.service.trabajo.PostulacionService;
import com.al_lado.al_lado_project.service.usuario.TrabajadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trabajadores")
@RequiredArgsConstructor
public class TrabajadorController {

    private final PostulacionService postulacionService;
    private final TrabajadorService trabajadorService;

    @GetMapping
    public ResponseEntity<List<TrabajadorDTO>> obtenerTodos() {
        return ResponseEntity.ok(trabajadorService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrabajadorDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(trabajadorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TrabajadorDTO> guardar(@Valid @RequestBody TrabajadorDTO trabajadorDTO) {
        return new ResponseEntity<>(trabajadorService.guardar(trabajadorDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrabajadorDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TrabajadorDTO trabajadorDTO) {
        trabajadorDTO.setId(id);
        return ResponseEntity.ok(trabajadorService.guardar(trabajadorDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        trabajadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/por-email/estadisticas")
    public ResponseEntity<EstadisticasTrabajadorDTO> obtenerEstadisticasPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(postulacionService.obtenerEstadisticasTrabajadorPorEmail(email));
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<EstadisticasTrabajadorDTO> obtenerEstadisticas(@PathVariable Long id) {
        return ResponseEntity.ok(postulacionService.obtenerEstadisticasTrabajador(id));
    }
}