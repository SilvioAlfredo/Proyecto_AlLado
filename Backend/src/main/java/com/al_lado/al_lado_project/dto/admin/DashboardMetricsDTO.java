package com.al_lado.al_lado_project.dto.admin;

import com.al_lado.al_lado_project.dto.trabajo.TendenciaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsDTO {

    private String totalIngresos;
    private Long metricContratos;
    private Long metricTrabajadores;
    private String metricAvg;
    private String dineroComunidad;
    private Long trabajosCompletados;
    private List<TendenciaDTO> tendencias;
}
