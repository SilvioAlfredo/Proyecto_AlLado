package com.al_lado.al_lado_project.controller.trabajo;

import com.al_lado.al_lado_project.dto.admin.PaginaDTO;
import com.al_lado.al_lado_project.dto.trabajo.ContactoEmpleadorDTO;
import com.al_lado.al_lado_project.dto.trabajo.FiltrosOportunidadesDTO;
import com.al_lado.al_lado_project.dto.trabajo.CandidatoMatchingDTO;
import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.service.trabajo.OportunidadLaboralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Listar oportunidades públicas", description = "Devuelve una página de oportunidades activas aplicando filtro de texto libre, paginación y metadatos (totalPages, totalElements).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de oportunidades",
            content = @Content(schema = @Schema(implementation = PaginaDTO.class))),
    })
    @GetMapping
    public ResponseEntity<PaginaDTO<OportunidadLaboralDTO>> listarOportunidades(
            @Parameter(description = "Texto libre: busca en título, contenido, ubicación o etiqueta")
            @RequestParam(required = false, defaultValue = "") String filtro,
            @Parameter(description = "Número de página (desde cero)")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OportunidadLaboralDTO> resultado = service.listarOportunidades(filtro, pageable);
        return ResponseEntity.ok(PaginaDTO.from(resultado));
    }

    @Operation(summary = "Obtener una oportunidad por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Oportunidad encontrada",
            content = @Content(schema = @Schema(implementation = OportunidadLaboralDTO.class))),
        @ApiResponse(responseCode = "404", description = "Oportunidad no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OportunidadLaboralDTO> obtenerOportunidad(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    /**
     * AL-SEC-006: el contacto del empleador no está en el detalle público de la
     * oferta. Exige sesión y relación con la oferta (titular, ADMIN o postulante);
     * el servicio decide y responde 403 si no corresponde.
     */
    @Operation(summary = "Obtener contacto del empleador de una oportunidad")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contacto del empleador",
            content = @Content(schema = @Schema(implementation = ContactoEmpleadorDTO.class))),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ver el contacto"),
        @ApiResponse(responseCode = "404", description = "Oportunidad no encontrada")
    })
    @GetMapping("/{id}/contacto")
    public ResponseEntity<ContactoEmpleadorDTO> obtenerContacto(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerContacto(id));
    }

    @Operation(summary = "Candidatos que matchan con la oportunidad (distancia)", description = "Exige rol EMPLEADOR o ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de candidatos",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CandidatoMatchingDTO.class)))),
        @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    })
    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @GetMapping("/{id}/matching")
    public ResponseEntity<List<CandidatoMatchingDTO>> candidatosMatching(
            @PathVariable Long id,
            @Parameter(description = "Radio de búsqueda en kilómetros")
            @RequestParam(defaultValue = "10") double radioKm) {
        return ResponseEntity.ok(service.candidatosParaOportunidad(id, radioKm));
    }

    @Operation(summary = "Crear una oferta de oportunidad", description = "Exige rol EMPLEADOR o ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Oferta creada",
            content = @Content(schema = @Schema(implementation = OportunidadLaboralDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    })
    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<OportunidadLaboralDTO> crearOferta(@Valid @RequestBody OportunidadLaboralDTO dto) {
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una oferta", description = "Exige rol EMPLEADOR o ADMIN y ser titular o ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Oferta actualizada",
            content = @Content(schema = @Schema(implementation = OportunidadLaboralDTO.class))),
        @ApiResponse(responseCode = "403", description = "No es el titular ni ADMIN"),
        @ApiResponse(responseCode = "404", description = "Oferta no encontrada")
    })
    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OportunidadLaboralDTO> actualizarOferta(@PathVariable Long id, @Valid @RequestBody OportunidadLaboralDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(service.guardar(dto));
    }

    @Operation(summary = "Cerrar (desactivar) una oferta", description = "Exige rol EMPLEADOR o ADMIN y ser titular o ADMIN. Reemplaza el flujo GET + PUT del frontend.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Oferta cerrada",
            content = @Content(schema = @Schema(implementation = OportunidadLaboralDTO.class))),
        @ApiResponse(responseCode = "403", description = "No es el titular ni ADMIN"),
        @ApiResponse(responseCode = "404", description = "Oferta no encontrada")
    })
    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<OportunidadLaboralDTO> cerrarOferta(@PathVariable Long id) {
        return ResponseEntity.ok(service.cerrarOportunidad(id));
    }

    @Operation(summary = "Eliminar una oferta", description = "Exige rol EMPLEADOR o ADMIN y ser titular o ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Oferta eliminada"),
        @ApiResponse(responseCode = "403", description = "No es el titular ni ADMIN"),
        @ApiResponse(responseCode = "404", description = "Oferta no encontrada")
    })
    @PreAuthorize("hasAnyRole('EMPLEADOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOferta(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar oportunidades con filtros avanzados y paginación", description = "Filtros: texto libre, modalidad, etiquetas, ubicación, sector, destacadas. Devuelve página con metadatos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de oportunidades filtradas",
            content = @Content(schema = @Schema(implementation = PaginaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Filtros inválidos")
    })
    @PostMapping("/filtrar")
    public ResponseEntity<PaginaDTO<OportunidadLaboralDTO>> listarConFiltros(
            @Valid @RequestBody FiltrosOportunidadesDTO filtros,
            @Parameter(description = "Número de página (desde cero)")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OportunidadLaboralDTO> resultado = service.listarConFiltros(filtros, pageable);
        return ResponseEntity.ok(PaginaDTO.from(resultado));
    }

    @PreAuthorize("hasRole('TRABAJADOR')")
    @GetMapping("/propuestas-pro")
    public ResponseEntity<List<OportunidadLaboralDTO>> propuestasPro(
            @RequestParam Long trabajadorId) {
        return ResponseEntity.ok(service.propuestasProParaTrabajador(trabajadorId));
    }
}
