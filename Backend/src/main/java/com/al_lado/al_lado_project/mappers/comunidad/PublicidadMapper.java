package com.al_lado.al_lado_project.mappers.comunidad;

import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;

import com.al_lado.al_lado_project.dto.comunidad.PublicidadDTO;
import com.al_lado.al_lado_project.entity.comunidad.Publicidad;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublicidadMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public PublicidadDTO toDTO(Publicidad entity) {
        if (entity == null) return null;

        PublicidadDTO dto = new PublicidadDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setContenido(entity.getContenido());
        dto.setUrlImagen(entity.getUrlImagen());
        dto.setUrlDestino(entity.getUrlDestino());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setActiva(entity.isActiva());
        dto.setAnunciante(usuarioResumenMapper.toDTO(entity.getAnunciante()));
        return dto;
    }

    /**
     * Proyección pública del anuncio (AL-SEC-006).
     *
     * <p>El anuncio es de lectura pública; el anunciante se identifica con id y
     * nombre, sin email ni teléfono.</p>
     *
     * @param entity entidad a convertir, o {@code null}
     * @return DTO con la proyección pública, o {@code null}
     */
    public PublicidadDTO toDTOPublic(Publicidad entity) {
        if (entity == null) return null;

        PublicidadDTO dto = new PublicidadDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setContenido(entity.getContenido());
        dto.setUrlImagen(entity.getUrlImagen());
        dto.setUrlDestino(entity.getUrlDestino());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setActiva(entity.isActiva());
        dto.setAnunciante(usuarioResumenMapper.toDTOPublic(entity.getAnunciante()));
        return dto;
    }

    public Publicidad toEntity(PublicidadDTO dto) {
        if (dto == null) return null;

        Publicidad entity = new Publicidad();
        // MEJ-003 / AL-SEC-008: el ID que manda el cliente no se copia en un alta.
        // Sólo la base de datos asigna IDs, de modo que ninguna petición puede
        // apuntar el alta a un registro existente y sobrescribirlo.
        entity.setId(null);
        entity.setTitulo(dto.getTitulo());
        entity.setContenido(dto.getContenido());
        entity.setUrlImagen(dto.getUrlImagen());
        entity.setUrlDestino(dto.getUrlDestino());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFin(dto.getFechaFin());
        entity.setActiva(dto.isActiva());
        return entity;
    }
}
