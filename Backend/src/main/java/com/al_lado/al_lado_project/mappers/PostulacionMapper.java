package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.PostulacionDTO;
import com.al_lado.al_lado_project.dto.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import org.springframework.stereotype.Component;

@Component
public class PostulacionMapper extends PostBaseMapper { // <-- Extiende de PostBaseMapper

    public PostulacionDTO toDTO(Postulacion entity) {
        if (entity == null) return null;
        PostulacionDTO dto = new PostulacionDTO();
        mapBaseToDTO(entity, dto); // Mapea campos heredados de PostBase
        dto.setPresupuesto(entity.getPresupuesto());
        dto.setDisponibilidad(entity.getDisponibilidad());
        dto.setEstado(entity.getEstado());

        if (entity.getOportunidad() != null) {
            OportunidadLaboralResumenDTO resumen = new OportunidadLaboralResumenDTO();
            resumen.setId(entity.getOportunidad().getId());
            resumen.setTitulo(entity.getOportunidad().getTitulo());
            dto.setOportunidad(resumen);
        }
        return dto;
    }

    public Postulacion toEntity(PostulacionDTO dto) {
        if (dto == null) return null;
        Postulacion entity = new Postulacion();
        mapBaseToEntity(dto, entity); // Mapea campos heredados de PostBase hacia la entidad
        entity.setPresupuesto(dto.getPresupuesto());
        entity.setDisponibilidad(dto.getDisponibilidad());
        entity.setEstado(dto.getEstado());
        // NO instancies OportunidadLaboral aquí; deja que el servicio se encargue por completo
        return entity;
    }
}