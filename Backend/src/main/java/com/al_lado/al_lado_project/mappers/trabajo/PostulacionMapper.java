package com.al_lado.al_lado_project.mappers.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostulacionMapper extends PostBaseMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

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
            OportunidadLaboralResumenDTO resumen = new OportunidadLaboralResumenDTO();
            resumen.setId(entity.getOportunidadLaboral().getId());
            resumen.setTitulo(entity.getOportunidadLaboral().getTitulo());
            resumen.setUbicacion(entity.getOportunidadLaboral().getUbicacion());
            resumen.setModalidad(entity.getOportunidadLaboral().getModalidad());
            resumen.setRangoSalarial(entity.getOportunidadLaboral().getRangoSalarial());
            resumen.setActiva(entity.getOportunidadLaboral().isActiva());
            resumen.setFechaCreacion(entity.getOportunidadLaboral().getFechaCreacion());
            dto.setOportunidadLaboral(resumen);
        }

        return dto;
    }

    public Postulacion toEntity(PostulacionDTO dto) {
        if (dto == null) return null;

        Postulacion entity = new Postulacion();
        mapBaseToEntity(dto, entity);

        entity.setRemuneracionPretendida(dto.getRemuneracionPretendida());
        entity.setEstado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE");

        return entity;
    }
}