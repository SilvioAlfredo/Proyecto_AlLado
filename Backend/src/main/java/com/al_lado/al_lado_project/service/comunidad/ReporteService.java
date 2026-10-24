package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.ReporteDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Registro y moderación de reportes de la comunidad.
 *
 * <p>El alta queda PENDIENTE. Puede pasar a revisión, resolverse o descartarse directamente.
 * RESUELTO y DESCARTADO son estados finales. El endpoint de moderación exige ADMIN.</p>
 */
public interface ReporteService {

    /** @param pageable página y orden; sin filtro de estado */
    Page<ReporteDTO> obtenerTodos(Pageable pageable);

    /**
     * @param id identificador del reporte
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException si no existe
     */
    ReporteDTO obtenerPorId(Long id);

    /**
     * @param estado    filtro por estado, sin distinguir mayúsculas
     * @param pageable página y orden
     */
    Page<ReporteDTO> obtenerPorEstado(String estado, Pageable pageable);

    /**
     * GH-56 (HU-13): reportes de un sector.
     *
     * @param sector   filtro por sector, sin distinguir mayúsculas; vacío o {@code null}
     *                 devuelve todos
     * @param pageable página y orden
     * @return reportes de ese sector; vacío si no hay coincidencias
     */
    Page<ReporteDTO> obtenerPorSector(String sector, Pageable pageable);

    /**
     * Registra un reporte PENDIENTE, aunque se reciba otro estado.
     *
     * <p>El reportante debe ser el usuario de la sesión o actuar bajo ADMIN. El usuario
     * reportado es opcional.</p>
     *
     * @param dto debe traer {@code reportante.id}; {@code reportado.id} es opcional
     * @return el reporte guardado
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si falta el reportante
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existen los usuarios referenciados
     * @throws org.springframework.security.access.AccessDeniedException
     *     si el reportante no es el de la sesión ni un ADMIN
     */
    ReporteDTO crear(ReporteDTO dto);

    /**
     * Pasa un reporte a revisión, lo resuelve o lo descarta.
     *
     * <p>Rechaza reportes RESUELTO o DESCARTADO. Si no llega moderadorId, usa al administrador
     * de la sesión; si llega, verifica que exista como administrador, sin exigir que coincida
     * con la sesión.</p>
     *
     * @param id identificador del reporte
     * @param moderadorId ID opcional del administrador que resuelve
     * @param nuevoEstado {@code EN_REVISION}, {@code RESUELTO} o {@code DESCARTADO}; {@code
     *     null} equivale a {@code RESUELTO}
     * @return el reporte con el estado normalizado a mayúsculas
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si ya estaba resuelto/descartado o el estado no es válido
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe el reporte o el administrador indicado
     */
    ReporteDTO resolver(Long id, Long moderadorId, String nuevoEstado);
}
