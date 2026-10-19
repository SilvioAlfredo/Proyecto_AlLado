package com.al_lado.al_lado_project.service.usuario;

import com.al_lado.al_lado_project.dto.admin.DashboardMetricsDTO;
import com.al_lado.al_lado_project.dto.usuario.AdministradorDTO;

import java.util.List;

public interface AdministradorService {
    List<AdministradorDTO> obtenerTodos();
    AdministradorDTO obtenerPorId(Long id);
    AdministradorDTO guardar(AdministradorDTO dto);
    void eliminar(Long id);

    /**
     * Obtiene los conteos y montos del Dashboard.
     *
     * <p>Los montos son estimaciones, no pagos confirmados:</p>
     * <ul><li>totalIngresos suma todos los aportes y 15.000 por cada PRO activo, sin comprobar
     * vencimiento ni convertir monedas.</li><li>metricContratos cuenta
     * postulaciones.</li><li>dineroComunidad suma los montos pretendidos en postulaciones
     * completadas.</li><li>Una oferta con varias etiquetas cuenta en cada una; sus porcentajes
     * pueden sumar más de 100.</li></ul>
     *
     * <p>Los importes se entregan como texto con formato monetario argentino, sin decimales.</p>
     */
    DashboardMetricsDTO obtenerMetricasDashboard();
}