package com.al_lado.al_lado_project.mappers.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostulacionMapper extends PostBaseMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;
    private final OportunidadLaboralResumenMapper oportunidadResumenMapper;

    public PostulacionDTO toDTO(Postulacion entity) {
        if (entity == null) return null;

        PostulacionDTO dto = new PostulacionDTO();
        mapBaseToDTO(entity, dto);

        if (entity.getAutor() != null) {
            dto.setAutor(usuarioResumenMapper.toDTO(entity.getAutor()));
        }

        dto.setRemuneracionPretendida(entity.getRemuneracionPretendida());
        dto.setEstado(entity.getEstado());

        if (entity.getOportunidadLaboral() != null) {
            dto.setOportunidadLaboral(oportunidadResumenMapper.toDTO(entity.getOportunidadLaboral()));
        }

        return dto;
    }

    public Postulacion toEntity(PostulacionDTO dto) {
        if (dto == null) return null;

        Postulacion entity = new Postulacion();
        mapBaseToEntity(dto, entity);

        entity.setRemuneracionPretendida(dto.getRemuneracionPretendida());
        entity.setEstado(dto.getEstado() != null ? dto.getEstado() : Postulacion.ESTADO_PENDIENTE);

        return entity;
    }
}