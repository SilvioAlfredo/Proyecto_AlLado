package com.al_lado.al_lado_project.service.usuario;

import com.al_lado.al_lado_project.dto.admin.DashboardMetricsDTO;
import com.al_lado.al_lado_project.dto.usuario.AdministradorDTO;

import java.util.List;

public interface AdministradorService {
    List<AdministradorDTO> obtenerTodos();
    AdministradorDTO obtenerPorId(Long id);
    AdministradorDTO guardar(AdministradorDTO dto);
    void eliminar(Long id);

    // Método para obtener las métricas consolidadas
    DashboardMetricsDTO obtenerMetricasDashboard();
}