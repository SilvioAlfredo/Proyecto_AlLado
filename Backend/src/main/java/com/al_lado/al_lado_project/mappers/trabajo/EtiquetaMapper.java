package com.al_lado.al_lado_project.mappers.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.EtiquetaDTO;
import com.al_lado.al_lado_project.entity.trabajo.Etiqueta;
import org.springframework.stereotype.Component;

@Component
public class EtiquetaMapper {

    public EtiquetaDTO toDTO(Etiqueta entity) {
        if (entity == null) return null;
        EtiquetaDTO dto = new EtiquetaDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        return dto;
    }

    public Etiqueta toEntity(EtiquetaDTO dto) {
        if (dto == null) return null;
        Etiqueta entity = new Etiqueta();
        // MEJ-003 / AL-SEC-008: el ID que manda el cliente no se copia en un alta.
        // Sólo la base de datos asigna IDs, de modo que ninguna petición puede
        // apuntar el alta a un registro existente y sobrescribirlo.
        entity.setId(null);
        entity.setNombre(dto.getNombre());
        return entity;
    }
}
