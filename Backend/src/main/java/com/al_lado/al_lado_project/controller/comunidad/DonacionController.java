package com.al_lado.al_lado_project.controller.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;
import com.al_lado.al_lado_project.service.comunidad.DonacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/donaciones")
@RequiredArgsConstructor
public class DonacionController {

    private final DonacionService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<DonacionDTO>> obtenerTodas(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.obtenerTodas(PageRequest.of(page, size)).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonacionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DonacionDTO>> obtenerPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.obtenerPorUsuario(usuarioId, PageRequest.of(page, size)).getContent());
    }

    @PostMapping
    public ResponseEntity<DonacionDTO> crear(@Valid @RequestBody DonacionDTO donacionDTO) {
        return new ResponseEntity<>(service.crear(donacionDTO), HttpStatus.CREATED);
    }

    /**
     * Admin verifica y asigna la suscripción/donación si el asunto y monto son correctos
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/verificar")
    public ResponseEntity<DonacionDTO> verificarTransferencia(
            @PathVariable Long id,
            @RequestParam String asunto,
            @RequestParam BigDecimal monto) {
        DonacionDTO resultado = service.verificarTransferencia(id, asunto, monto);
        return ResponseEntity.ok(resultado);
    }
}