package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.PublicidadDTO;

import java.util.List;

/**
 * Anuncios de empleadores y sus fechas de publicación.
 */
public interface PublicidadService {

    /** @return todas las publicidades, activas o no, sin paginación. */
    List<PublicidadDTO> obtenerTodas();


    PublicidadDTO obtenerPorId(Long id);

    /**
     * @param anuncianteId ID del empleador anunciante
     * @return sus publicidades, sin filtrar por vigencia; vacío si no tiene
     */
    List<PublicidadDTO> obtenerPorAnunciante(Long anuncianteId);

    /**
     * Devuelve anuncios activos dentro de sus fechas de publicación.
     *
     * <p>El anuncio se muestra tanto el día de inicio como el de finalización.</p>
     *
     * @return lista vacía si no hay campañas vigentes
     */
    List<PublicidadDTO> obtenerVigentes();

    /**
     * Crea o actualiza un anuncio de un empleador con PRO vigente.
     *
     * <p>Requiere al titular o ADMIN. La fecha final debe ser igual o posterior al inicio. El
     * alta queda activa; la edición toma activa del DTO. Solo ADMIN puede cambiar el
     * anunciante.</p>
     *
     * @param dto sin {@code id} para crear; con {@code id} para editar
     * @return la publicidad persistida
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si faltan datos, las fechas se invierten, no hay PRO o se intenta transferir
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existen la publicidad o el anunciante
     * @throws org.springframework.security.access.AccessDeniedException
     *     si actúa un tercero sin rol ADMIN
     */
    PublicidadDTO guardar(PublicidadDTO dto);

    /**
     * Elimina un anuncio. Solo el anunciante titular o ADMIN.
     *
     * @param id identificador de la publicidad
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe
     */
    void eliminar(Long id);
}
