package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.PostulacionDTO;
import com.al_lado.al_lado_project.dto.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.entity.Postulacion;
import com.al_lado.al_lado_project.entity.OportunidadLaboral;
import org.springframework.stereotype.Component;

@Component
public class PostulacionMapper {

    public PostulacionDTO toDTO(Postulacion entity) {
        if (entity == null) return null;

        PostulacionDTO dto = new PostulacionDTO();
        dto.setId(entity.getId());
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
        entity.setId(dto.getId());
        entity.setPresupuesto(dto.getPresupuesto());
        entity.setDisponibilidad(dto.getDisponibilidad());
        entity.setEstado(dto.getEstado());

        //
        if (dto.getOportunidad() != null) {
            OportunidadLaboral oportunidad = new OportunidadLaboral();
            oportunidad.setId(dto.getOportunidad().getId());
            entity.setOportunidad(oportunidad);
        }

        return entity;
    }
}