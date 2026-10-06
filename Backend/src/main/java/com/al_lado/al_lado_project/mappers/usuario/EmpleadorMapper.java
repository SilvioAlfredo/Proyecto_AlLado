package com.al_lado.al_lado_project.mappers.usuario;

import com.al_lado.al_lado_project.dto.usuario.EmpleadorDTO;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.mappers.comunidad.PerfilEmpresarialMapper;
import com.al_lado.al_lado_project.mappers.trabajo.OportunidadLaboralResumenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmpleadorMapper {

    private final PerfilEmpresarialMapper perfilEmpresarialMapper;
    private final OportunidadLaboralResumenMapper oportunidadResumenMapper;

    public EmpleadorDTO toDTO(Empleador entity) {
        if (entity == null) return null;

        EmpleadorDTO dto = new EmpleadorDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());

        if (entity.getPerfilEmpresarial() != null) {
            dto.setPerfilEmpresarial(perfilEmpresarialMapper.toDTO(entity.getPerfilEmpresarial()));
        }

        if (entity.getOportunidades() != null) {
            dto.setOportunidades(entity.getOportunidades().stream()
                    .map(oportunidadResumenMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Empleador toEntity(EmpleadorDTO dto) {
        if (dto == null) return null;

        Empleador entity = new Empleador();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setPassword(dto.getPassword());
        return entity;
    }
}