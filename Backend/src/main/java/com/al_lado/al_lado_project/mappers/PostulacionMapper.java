package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.dto.PostulacionDTO;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import org.springframework.stereotype.Component;

@Component
public class PostulacionMapper extends PostBaseMapper {

    public PostulacionDTO toDTO(Postulacion entity) {
        if (entity == null) return null;

        PostulacionDTO dto = new PostulacionDTO();
        mapBaseToDTO(entity, dto);

        dto.setPresupuesto(entity.getRemuneracionPretendida());
        dto.setEstado(entity.getEstado());

        if (entity.getOportunidadLaboral() != null) {
            OportunidadLaboralResumenDTO resumen = new OportunidadLaboralResumenDTO();
            resumen.setId(entity.getOportunidadLaboral().getId());
            resumen.setTitulo(entity.getOportunidadLaboral().getTitulo());
            dto.setOportunidad(resumen);
        }

        return dto;
    }

    public Postulacion toEntity(PostulacionDTO dto) {
        if (dto == null) return null;

        Postulacion entity = new Postulacion();
        mapBaseToEntity(dto, entity);

        entity.setRemuneracionPretendida(dto.getPresupuesto());
        entity.setEstado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE");

        return entity;
    }
}