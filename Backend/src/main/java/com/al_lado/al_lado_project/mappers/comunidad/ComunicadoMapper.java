package com.al_lado.al_lado_project.mappers.comunidad;

import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComunicadoMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public ComunicadoDTO toDTO(Comunicado entity) {
        if (entity == null) return null;

        ComunicadoDTO dto = new ComunicadoDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setContenido(entity.getContenido());
        dto.setAlcance(entity.getAlcance());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setEmisor(usuarioResumenMapper.toDTO(entity.getEmisor()));
        return dto;
    }

    /**
     * Proyección pública del comunicado (AL-SEC-006).
     *
     * <p>El comunicado lo emite la administración: se conserva quién lo emitió con
     * id y nombre, pero el email de esa cuenta no es público.</p>
     *
     * @param entity entidad a convertir, o {@code null}
     * @return DTO con la proyección pública, o {@code null}
     */
    public ComunicadoDTO toDTOPublic(Comunicado entity) {
        if (entity == null) return null;

        ComunicadoDTO dto = new ComunicadoDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setContenido(entity.getContenido());
        dto.setAlcance(entity.getAlcance());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setEmisor(usuarioResumenMapper.toDTOPublic(entity.getEmisor()));
        return dto;
    }

    // La relacion emisor se resuelve en ComunicadoServiceImpl por ID;
    // no se reconstruye aqui para no crear entidades transient.
    public Comunicado toEntity(ComunicadoDTO dto) {
        if (dto == null) return null;

        Comunicado entity = new Comunicado();
        // MEJ-003 / AL-SEC-008: el ID que manda el cliente no se copia en un alta.
        // Sólo la base de datos asigna IDs, de modo que ninguna petición puede
        // apuntar el alta a un registro existente y sobrescribirlo.
        entity.setId(null);
        entity.setTitulo(dto.getTitulo());
        entity.setContenido(dto.getContenido());
        entity.setAlcance(dto.getAlcance());
        return entity;
    }
}
