package com.al_lado.al_lado_project.mappers.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OportunidadLaboralMapper extends PostBaseMapper {

    private final EtiquetaMapper etiquetaMapper;
    private final PostulacionResumenMapper postulacionResumenMapper;
    private final UsuarioResumenMapper usuarioResumenMapper;

    public OportunidadLaboralDTO toDTO(OportunidadLaboral entity) {
        if (entity == null) return null;

        OportunidadLaboralDTO dto = new OportunidadLaboralDTO();
        mapBaseToDTO(entity, dto);

        if (entity.getAutor() != null) {
            dto.setAutor(usuarioResumenMapper.toDTO(entity.getAutor()));
        }

        dto.setUbicacion(entity.getUbicacion());
        dto.setModalidad(entity.getModalidad());
        dto.setRangoSalarial(entity.getRangoSalarial());
        dto.setActiva(entity.isActiva());

        if (entity.getEtiquetas() != null) {
            dto.setEtiquetas(entity.getEtiquetas().stream()
                    .map(etiquetaMapper::toDTO)
                    .collect(Collectors.toSet()));
        }

        if (entity.getPostulaciones() != null) {
            dto.setPostulaciones(entity.getPostulaciones().stream()
                    .map(postulacionResumenMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public OportunidadLaboral toEntity(OportunidadLaboralDTO dto) {
        if (dto == null) return null;

        OportunidadLaboral entity = new OportunidadLaboral();
        mapBaseToEntity(dto, entity);

        entity.setUbicacion(dto.getUbicacion());
        entity.setModalidad(dto.getModalidad());
        entity.setRangoSalarial(dto.getRangoSalarial());
        entity.setActiva(dto.isActiva());

        if (dto.getEtiquetas() != null) {
            entity.setEtiquetas(dto.getEtiquetas().stream()
                    .map(etiquetaMapper::toEntity)
                    .collect(Collectors.toSet()));
        }

        return entity;
    }
}