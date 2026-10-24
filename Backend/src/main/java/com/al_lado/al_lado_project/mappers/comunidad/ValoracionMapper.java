package com.al_lado.al_lado_project.mappers.comunidad;

import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;

import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValoracionMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public ValoracionDTO toDTO(Valoracion entity) {
        if (entity == null) return null;

        ValoracionDTO dto = new ValoracionDTO();
        dto.setId(entity.getId());
        dto.setPuntuacion(entity.getPuntuacion());
        dto.setComentario(entity.getComentario());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setAutor(usuarioResumenMapper.toDTO(entity.getAutor()));
        dto.setTrabajador(usuarioResumenMapper.toDTO(entity.getTrabajador()));
        return dto;
    }

    /**
     * Proyección pública de la valoración (AL-SEC-006).
     *
     * <p>Conserva la puntuación, el comentario y quién valora o quién es valorado,
     * pero sólo con id y nombre: el email no es público.</p>
     *
     * @param entity entidad a convertir, o {@code null}
     * @return DTO con la proyección pública, o {@code null}
     */
    public ValoracionDTO toDTOPublic(Valoracion entity) {
        if (entity == null) return null;

        ValoracionDTO dto = new ValoracionDTO();
        dto.setId(entity.getId());
        dto.setPuntuacion(entity.getPuntuacion());
        dto.setComentario(entity.getComentario());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setAutor(usuarioResumenMapper.toDTOPublic(entity.getAutor()));
        dto.setTrabajador(usuarioResumenMapper.toDTOPublic(entity.getTrabajador()));
        return dto;
    }

    // Las relaciones autor/trabajador se resuelven en ValoracionServiceImpl
    // por ID; no se reconstruyen aqui para no crear entidades transient.
    public Valoracion toEntity(ValoracionDTO dto) {
        if (dto == null) return null;

        Valoracion entity = new Valoracion();
        // MEJ-003 / AL-SEC-008: el ID que manda el cliente no se copia en un alta.
        // Sólo la base de datos asigna IDs, de modo que ninguna petición puede
        // apuntar el alta a un registro existente y sobrescribirlo.
        entity.setId(null);
        entity.setPuntuacion(dto.getPuntuacion());
        entity.setComentario(dto.getComentario());
        return entity;
    }
}
