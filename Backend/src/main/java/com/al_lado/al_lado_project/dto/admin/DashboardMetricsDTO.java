package com.al_lado.al_lado_project.dto.admin;

import com.al_lado.al_lado_project.dto.trabajo.TendenciaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Métricas del dashboard administrativo.
 *
 * <p>Ningún campo es una cifra auditada: ver
 * {@link com.al_lado.al_lado_project.service.usuario.AdministradorService#obtenerMetricasDashboard()}
 * para el criterio de cálculo de cada uno.</p>
 *
 * <p>Los importes ({@code totalIngresos}, {@code metricAvg}, {@code dineroComunidad})
 * llegan <b>formateados como String</b>, no como números.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsDTO {

    /** Suma de todas las donaciones + 15.000 por cada plan PRO activo; estimación, no cobro real. */
    private String totalIngresos;
    /** Cantidad de postulaciones (no de contratos firmados). */
    private Long metricContratos;
    private Long metricTrabajadores;
    /** Promedio de remuneración pretendida de las postulaciones con monto positivo, como texto. */
    private String metricAvg;
    /** Suma de remuneraciones pretendidas de postulaciones COMPLETADA: valor declarado, no pagado. */
    private String dineroComunidad;
    /** Postulaciones en estado COMPLETADA. */
    private Long trabajosCompletados;
    /** Top 5 de etiquetas por cantidad de oportunidades; categorías no excluyentes (una oferta puede sumar en varias). */
    private List<TendenciaDTO> tendencias;

    // HU admin "Visualizar estado del sistema": usuarios activos/inactivos.
    private Long usuariosActivos;
    private Long usuariosInactivos;

    // HU admin: postulaciones por estado (PENDIENTE, SELECCIONADA,
    // RECHAZADA, COMPLETADA, RETIRADA).
    private Map<String, Long> postulacionesPorEstado;
}
