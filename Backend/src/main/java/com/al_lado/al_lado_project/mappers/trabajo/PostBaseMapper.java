package com.al_lado.al_lado_project.mappers.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.PostBaseDTO;
import com.al_lado.al_lado_project.entity.trabajo.PostBase;

/**
 * Copia los campos comunes de ofertas y postulaciones.
 *
 * <p>No asigna el autor. Al convertir a entidad tampoco copia fechaCreacion; la fecha de una
 * entidad nueva se completa en {@code @PrePersist}.</p>
 */
public abstract class PostBaseMapper {

    /**
     * Copia ID, título, contenido y fecha de creación al DTO.
     *
     * <p>No toca {@code autor}: quien necesite el propietario debe resolverlo por
     * separado (los DTO de resumen lo traen ya convertido).</p>
     */
    protected void mapBaseToDTO(PostBase entity, PostBaseDTO dto) {
        if (entity == null || dto == null) return;
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setContenido(entity.getContenido());
        dto.setFechaCreacion(entity.getFechaCreacion());
    }

    /**
     * Copia ID, título y contenido; no modifica autor ni fecha de creación.
     *
     * <p>El ID recibido se conserva. Para un alta, el DTO debe llegar sin ID.</p>
     */
    protected void mapBaseToEntity(PostBaseDTO dto, PostBase entity) {
        if (dto == null || entity == null) return;
        entity.setId(dto.getId());
        entity.setTitulo(dto.getTitulo());
        entity.setContenido(dto.getContenido());
    }
}