package com.al_lado.al_lado_project.dto.usuario;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Estadísticas del perfil de un trabajador.
 *
 * <p>Incluye métricas calculadas desde postulaciones y valoraciones registradas.</p>
 * <ul>
 *   <li><b>Reales:</b> {@code valoracionesCount}, {@code promedioValoraciones} (0 si
 *       no hay valoraciones), {@code postulacionesEnviadas/seleccionadas/completadas},
 *       {@code perfilCompleto} (según tenga fotografía) y {@code suscripcionActiva}.</li>
 *   <li><b>Estimados:</b> los ingresos suman remuneraciones pretendidas en postulaciones
 *       completadas (no pagos auditados) y solo se entregan con PRO vigente.</li>
 *   <li><b>Aún no implementados:</b> {@code ofertasPublicadas} siempre en 0 (inaplicable al rol),
 *       {@code nivelRecomendacion} siempre 4 y {@code ultimoLogin} siempre nulo.</li>
 * </ul>
 *
 * <p>Los datos financieros son estimaciones, no acreditan pagos.</p>
 */
@Data
public class EstadisticasTrabajadorDTO {

    private Long id;
    private String nombre;
    private String email;

    // Métricas financieras
    private BigDecimal ingresosTotales;
    private BigDecimal ingresosUltimos3Meses;
    private boolean ingresosDisponibles;
    private Integer valoracionesCount;
    private BigDecimal promedioValoraciones; // 1-5 estrellas

    // Métricas de actividad
    private Integer ofertasPublicadas;
    private Integer postulacionesEnviadas;
    private Integer postulacionesSeleccionadas;
    private Integer postulacionesCompletadas;

    // Métricas de perfil
    private Integer nivelRecomendacion; // 1-5
    private boolean perfilCompleto;

    // Metadatos
    private String ultimoLogin;
    private boolean suscripcionActiva;
}
