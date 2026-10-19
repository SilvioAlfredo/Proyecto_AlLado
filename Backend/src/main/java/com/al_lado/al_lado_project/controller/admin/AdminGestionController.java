package com.al_lado.al_lado_project.controller.admin;

import com.al_lado.al_lado_project.dto.admin.PaginaDTO;
import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;
import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;
import com.al_lado.al_lado_project.dto.comunidad.ReporteDTO;
import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;
import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;
import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.service.admin.AdminGestionService;
import com.al_lado.al_lado_project.service.trabajo.OportunidadLaboralService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminGestionController {
    private final AdminGestionService gestion;
    private final OportunidadLaboralService oportunidades;

    private PageRequest page(int page, int size, Sort sort) {
        if (page < 0 || size < 1 || size > 100) throw new BusinessRuleException("Paginación inválida: página desde 0 y tamaño entre 1 y 100.");
        return PageRequest.of(page, size, sort);
    }

    @GetMapping("/comunicados")
    public PaginaDTO<ComunicadoDTO> comunicados(@RequestParam(defaultValue = "") String filtro,
            @RequestParam(defaultValue = "") String alcance, @RequestParam(defaultValue = "recientes") String orden,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Sort sort = switch (orden) {
            case "recientes" -> Sort.by(Sort.Direction.DESC, "fechaEmision").and(Sort.by(Sort.Direction.DESC, "id"));
            case "antiguos" -> Sort.by("fechaEmision", "id");
            case "titulo" -> Sort.by("titulo", "id");
            default -> throw new BusinessRuleException("Orden inválido.");
        };
        return PaginaDTO.from(gestion.comunicados(filtro, alcance, page(page, size, sort)));
    }

    @GetMapping("/oportunidades")
    public PaginaDTO<OportunidadLaboralDTO> oportunidades(@RequestParam(defaultValue = "") String filtro,
            @RequestParam(required = false) String modalidad, @RequestParam(required = false) List<String> etiquetas,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return PaginaDTO.from(oportunidades.listarOportunidades(filtro, modalidad, etiquetas,
                page(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion", "id"))));
    }

    @GetMapping("/oportunidades/{id}/postulaciones")
    public PaginaDTO<PostulacionDTO> postulaciones(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return PaginaDTO.from(gestion.postulaciones(id, page(page, size, Sort.by("fechaCreacion", "id"))));
    }

    @GetMapping("/reportes")
    public PaginaDTO<ReporteDTO> reportes(@RequestParam(defaultValue = "") String filtro,
            @RequestParam(defaultValue = "PENDIENTE") String estado,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        if (!java.util.Set.of("TODOS", "PENDIENTE", "EN_REVISION", "RESUELTO", "DESCARTADO").contains(estado))
            throw new BusinessRuleException("Estado de reporte inválido.");
        return PaginaDTO.from(gestion.reportes(filtro, estado, page(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion", "id"))));
    }

    @GetMapping("/foro")
    public PaginaDTO<PublicacionForoDTO> foro(@RequestParam(defaultValue = "") String filtro,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return PaginaDTO.from(gestion.foro(filtro, page(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion", "id"))));
    }

    @GetMapping("/valoraciones")
    public PaginaDTO<ValoracionDTO> valoraciones(@RequestParam Long trabajadorId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        if (trabajadorId <= 0) throw new BusinessRuleException("Trabajador inválido.");
        return PaginaDTO.from(gestion.valoraciones(trabajadorId, page(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion", "id"))));
    }

    @GetMapping("/donaciones")
    public PaginaDTO<DonacionDTO> donaciones(@RequestParam(defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODAS") String estado,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        if (!java.util.Set.of("TODAS", "PENDIENTE", "VERIFICADO", "RECHAZADO").contains(estado))
            throw new BusinessRuleException("Estado de transferencia inválido.");
        return PaginaDTO.from(gestion.donaciones(filtro, estado, page(page, size, Sort.by(Sort.Direction.DESC, "fechaDonacion", "id"))));
    }

    @GetMapping("/suscripciones")
    public PaginaDTO<SuscripcionDTO> suscripciones(@RequestParam(defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODAS") String estado,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        if (!java.util.Set.of("TODAS", "ACTIVAS", "INACTIVAS").contains(estado))
            throw new BusinessRuleException("Estado de suscripción inválido.");
        return PaginaDTO.from(gestion.suscripciones(filtro, estado, page(page, size, Sort.by(Sort.Direction.DESC, "fechaInicio", "id"))));
    }
    @GetMapping("/publicidades")
    public PaginaDTO<com.al_lado.al_lado_project.dto.comunidad.PublicidadDTO> publicidades(@RequestParam(defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODAS") String estado,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        if (!java.util.Set.of("TODAS", "VIGENTES", "INACTIVAS", "VENCIDAS", "PROGRAMADAS").contains(estado)) throw new BusinessRuleException("Estado de publicidad inválido.");
        return PaginaDTO.from(gestion.publicidades(filtro, estado, page(page, size, Sort.by(Sort.Direction.DESC, "fechaInicio", "id"))));
    }

    @GetMapping("/empresas")
    public PaginaDTO<com.al_lado.al_lado_project.dto.admin.EmpresaAdminDTO> empresas(@RequestParam(defaultValue = "") String filtro,
            @RequestParam(defaultValue = "TODAS") String estado,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        if (!java.util.Set.of("TODAS", "VERIFICADAS", "PENDIENTES", "SIN_PERFIL").contains(estado)) throw new BusinessRuleException("Estado de empresa inválido.");
        return PaginaDTO.from(gestion.empresas(filtro, estado, page(page, size, Sort.by("nombre", "id"))));
    }
}
