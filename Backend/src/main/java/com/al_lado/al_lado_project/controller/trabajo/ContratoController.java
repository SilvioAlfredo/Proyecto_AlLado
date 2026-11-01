package com.al_lado.al_lado_project.controller.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.ContratoDTO;
import com.al_lado.al_lado_project.service.trabajo.ContratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoService service;

    /**
     * HU-08 CA-01: Historial de contratos del empleador autenticado.
     * Devuelve todas las postulaciones de sus ofertas, ordenadas por fecha descendente.
     */
    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @GetMapping("/historial/empleador")
    public ResponseEntity<List<ContratoDTO>> historialEmpleador() {
        return ResponseEntity.ok(service.historialEmpleador());
    }

    /**
     * Historial de contratos de un trabajador: sus postulaciones con estados de
     * ejecución (SELECCIONADA, COMPLETADA, RETIRADA, RECHAZADA).
     */
    @PreAuthorize("hasAnyRole('TRABAJADOR', 'EMPLEADOR', 'ADMIN')")
    @GetMapping("/historial/trabajador/{trabajadorId:[0-9]+}")
    public ResponseEntity<List<ContratoDTO>> historialTrabajador(@PathVariable Long trabajadorId) {
        return ResponseEntity.ok(service.historialTrabajador(trabajadorId));
    }

    /**
     * Detalle de un contrato/postulación por su ID.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<ContratoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }
}
