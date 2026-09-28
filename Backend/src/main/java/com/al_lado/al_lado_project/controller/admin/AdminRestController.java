package com.al_lado.al_lado_project.controller.admin;

import com.al_lado.al_lado_project.dto.admin.DashboardMetricsDTO;
import com.al_lado.al_lado_project.service.usuario.AdministradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminRestController {

    private final AdministradorService administradorService;

    @GetMapping("/dashboard/metrics")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics() {
        DashboardMetricsDTO dto = administradorService.obtenerMetricasDashboard();
        return ResponseEntity.ok(dto);
    }
}