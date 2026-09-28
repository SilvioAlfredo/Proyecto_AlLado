package com.al_lado.al_lado_project.dto.admin;

import com.al_lado.al_lado_project.dto.trabajo.TendenciaDTO;
import java.util.List;

public class DashboardMetricsDTO {

    private String totalIngresos;
    private Long metricContratos;
    private Long metricTrabajadores;
    private String metricAvg;
    private String dineroComunidad;
    private Long trabajosCompletados;
    private List<TendenciaDTO> tendencias;

    public DashboardMetricsDTO() {
    }

    public DashboardMetricsDTO(String totalIngresos, Long metricContratos, Long metricTrabajadores,
                               String metricAvg, String dineroComunidad, Long trabajosCompletados,
                               List<TendenciaDTO> tendencias) {
        this.totalIngresos = totalIngresos;
        this.metricContratos = metricContratos;
        this.metricTrabajadores = metricTrabajadores;
        this.metricAvg = metricAvg;
        this.dineroComunidad = dineroComunidad;
        this.trabajosCompletados = trabajosCompletados;
        this.tendencias = tendencias;
    }

    // Getters y Setters
    public String getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(String totalIngresos) { this.totalIngresos = totalIngresos; }

    public Long getMetricContratos() { return metricContratos; }
    public void setMetricContratos(Long metricContratos) { this.metricContratos = metricContratos; }

    public Long getMetricTrabajadores() { return metricTrabajadores; }
    public void setMetricTrabajadores(Long metricTrabajadores) { this.metricTrabajadores = metricTrabajadores; }

    public String getMetricAvg() { return metricAvg; }
    public void setMetricAvg(String metricAvg) { this.metricAvg = metricAvg; }

    public String getDineroComunidad() { return dineroComunidad; }
    public void setDineroComunidad(String dineroComunidad) { this.dineroComunidad = dineroComunidad; }

    public Long getTrabajosCompletados() { return trabajosCompletados; }
    public void setTrabajosCompletados(Long trabajosCompletados) { this.trabajosCompletados = trabajosCompletados; }

    public List<TendenciaDTO> getTendencias() { return tendencias; }
    public void setTendencias(List<TendenciaDTO> tendencias) { this.tendencias = tendencias; }
}
