package com.al_lado.al_lado_project.mappers.perfil;

import com.al_lado.al_lado_project.dto.perfil.HorarioDisponibilidadDTO;
import com.al_lado.al_lado_project.entity.perfil.HorarioDisponibilidad;
import org.springframework.stereotype.Component;

@Component
public class HorarioDisponibilidadMapper {

    public HorarioDisponibilidadDTO toDTO(HorarioDisponibilidad entity) {
        if (entity == null) return null;

        HorarioDisponibilidadDTO dto = new HorarioDisponibilidadDTO();
        dto.setId(entity.getId());
        dto.setDiaSemana(entity.getDiaSemana());
        dto.setHoraInicio(entity.getHoraInicio());
        dto.setHoraFin(entity.getHoraFin());
        return dto;
    }

    public HorarioDisponibilidad toEntity(HorarioDisponibilidadDTO dto) {
        if (dto == null) return null;

        HorarioDisponibilidad entity = new HorarioDisponibilidad();
        // MEJ-003 / AL-SEC-008: el ID que manda el cliente no se copia en un alta.
        // Sólo la base de datos asigna IDs, de modo que ninguna petición puede
        // apuntar el alta a un registro existente y sobrescribirlo.
        entity.setId(null);
        entity.setDiaSemana(dto.getDiaSemana());
        entity.setHoraInicio(dto.getHoraInicio());
        entity.setHoraFin(dto.getHoraFin());
        return entity;
    }
}
