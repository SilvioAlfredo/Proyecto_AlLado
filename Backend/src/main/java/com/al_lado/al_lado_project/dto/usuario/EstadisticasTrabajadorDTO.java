package com.al_lado.al_lado_project.dto.usuario;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Estadísticas del perfil de un trabajador.
 *
 * <p>Mezcla métricas reales con <b>datos provisionales</b> fijos que deben leerse como
 * «aún no implementado», no como valor de negocio:</p>
 * <ul>
 *   <li><b>Reales:</b> {@code valoracionesCount}, {@code promedioValoraciones} (0 si
 *       no hay valoraciones), {@code postulacionesEnviadas/seleccionadas/completadas},
 *       {@code perfilCompleto} (según tenga fotografía) y {@code suscripcionActiva}
 *       (solo el indicador {@code activa}, sin comprobar vencimiento ni nivel PRO).</li>
 *   <li><b>Aún no implementados:</b> {@code ingresosTotales} e {@code ingresosUltimos3Meses}
 *       siempre en cero, {@code ofertasPublicadas} siempre en 0 (inaplicable al rol),
 *       {@code nivelRecomendacion} siempre 4 y {@code ultimoLogin} siempre nulo.</li>
 * </ul>
 *
 * <p>Los datos financieros no acreditan pagos. La completa
 * {@code PostulacionServiceImpl#obtenerEstadisticasTrabajador}.</p>
 */
@Data
public class EstadisticasTrabajadorDTO {

    private Long id;
    private String nombre;
    private String email;

    // Métricas financieras
    private BigDecimal ingresosTotales;
    private BigDecimal ingresosUltimos3Meses;
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