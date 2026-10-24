package com.al_lado.al_lado_project.controller.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.ContactoEmpleadorDTO;
import com.al_lado.al_lado_project.dto.trabajo.FiltrosOportunidadesDTO;
import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.service.trabajo.OportunidadLaboralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/oportunidades")
@RequiredArgsConstructor
public class OportunidadLaboralController {

    private final OportunidadLaboralService service;

    @GetMapping
    public ResponseEntity<List<OportunidadLaboralDTO>> listarOportunidades(
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OportunidadLaboralDTO> resultado = service.listarOportunidades(filtro, pageable);
        return ResponseEntity.ok(resultado.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OportunidadLaboralDTO> obtenerOportunidad(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    /**
     * AL-SEC-006: el contacto del empleador no está en el detalle público de la
     * oferta. Exige sesión y relación con la oferta (titular, ADMIN o postulante);
     * el servicio decide y responde 403 si no corresponde.
     */
    @GetMapping("/{id}/contacto")
    public ResponseEntity<ContactoEmpleadorDTO> obtenerContacto(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerContacto(id));
    }

    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<OportunidadLaboralDTO> crearOferta(@Valid @RequestBody OportunidadLaboralDTO dto) {
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OportunidadLaboralDTO> actualizarOferta(@PathVariable Long id, @Valid @RequestBody OportunidadLaboralDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(service.guardar(dto));
    }

    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOferta(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filtrar")
    public ResponseEntity<List<OportunidadLaboralDTO>> listarConFiltros(
            @Valid @RequestBody FiltrosOportunidadesDTO filtros
    ) {
        List<OportunidadLaboralDTO> resultado = service.listarConFiltros(filtros);
        return ResponseEntity.ok(resultado);
    }

    @PreAuthorize("hasRole('TRABAJADOR')")
    @GetMapping("/propuestas-pro")
    public ResponseEntity<List<OportunidadLaboralDTO>> propuestasPro(
            @RequestParam Long trabajadorId) {
        return ResponseEntity.ok(service.propuestasProParaTrabajador(trabajadorId));
    }
}