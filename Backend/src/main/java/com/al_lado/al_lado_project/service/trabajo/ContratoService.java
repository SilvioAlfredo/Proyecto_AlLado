package com.al_lado.al_lado_project.service.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.ContratoDTO;

import java.util.List;

/**
 * Servicio de historial de contratos (HU-08).
 *
 * <p>Un contrato se entiende como una postulación que ha avanzado más allá del
 * estado inicial {@code PENDIENTE}. Este servicio expone el historial de
 * contratos para el empleador (contratos de sus ofertas) y para el trabajador
 * (sus propias postulaciones con estados de ejecución).
 */
public interface ContratoService {

    /**
     * Historial de contratos del empleador autenticado: todas las postulaciones
     * recibidas en sus ofertas laborales, ordenadas por fecha de creación
     * descendente.
     *
     * @return lista de contratos (puede estar vacía)
     */
    List<ContratoDTO> historialEmpleador();

    /**
     * Historial de contratos de un trabajador: todas sus postulaciones con
     * estados de ejecución ({@code SELECCIONADA}, {@code COMPLETADA},
     * {@code RETIRADA}, {@code RECHAZADA}), ordenadas por fecha descendente.
     *
     * @param trabajadorId ID del trabajador
     * @return lista de contratos
     */
    List<ContratoDTO> historialTrabajador(Long trabajadorId);

    /**
     * Detalle de un contrato/postulación por su ID.
     *
     * @param id ID del contrato
     * @return el contrato encontrado
     */
    ContratoDTO obtenerPorId(Long id);
}
