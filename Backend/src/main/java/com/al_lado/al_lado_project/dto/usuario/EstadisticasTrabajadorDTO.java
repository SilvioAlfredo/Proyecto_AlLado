package com.al_lado.al_lado_project.dto.usuario;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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