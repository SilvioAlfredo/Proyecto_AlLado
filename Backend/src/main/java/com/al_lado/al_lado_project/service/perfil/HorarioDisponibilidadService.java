package com.al_lado.al_lado_project.service.perfil;

import com.al_lado.al_lado_project.dto.perfil.HorarioDisponibilidadDTO;

import java.util.List;

/**
 * Horarios de disponibilidad declarados por un trabajador.
 */
public interface HorarioDisponibilidadService {

    /**
     * @param trabajadorId ID del trabajador dueño de los horarios
     * @return sus horarios; vacío si no registró ninguno (sin comprobación de titularidad)
     */
    List<HorarioDisponibilidadDTO> obtenerPorTrabajador(Long trabajadorId);

    /**
     * Agrega un horario del trabajador titular o de una cuenta administrada por ADMIN.
     *
     * <p>Exige día y horas; el inicio debe ser anterior al fin. Rechaza cruces con horarios
     * existentes del mismo día. Uno puede comenzar cuando termina otro. No admite horarios que
     * crucen medianoche.</p>
     *
     * <p>La comprobación no bloquea altas simultáneas; dos solicitudes a la vez podrían
     * registrar horarios superpuestos.</p>
     *
     * @param trabajadorId ID del titular; puede ser de otro usuario si quien ejecuta es ADMIN
     * @param dto día, hora de inicio y hora de fin
     * @return el horario guardado
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si faltan datos, {@code inicio ≥ fin} o hay solape
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe el trabajador
     * @throws org.springframework.security.access.AccessDeniedException
     *     si actúa un tercero sin rol ADMIN
     */
    HorarioDisponibilidadDTO agregar(Long trabajadorId, HorarioDisponibilidadDTO dto);

    /**
     * Elimina un horario. Solo el trabajador titular o ADMIN.
     *
     * @param id identificador del horario
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe
     */
    void eliminar(Long id);
}
