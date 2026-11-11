package com.al_lado.al_lado_project.mappers.comunidad;

import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;

import com.al_lado.al_lado_project.dto.comunidad.PerfilEmpresarialDTO;
import com.al_lado.al_lado_project.entity.comunidad.PerfilEmpresarial;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerfilEmpresarialMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public PerfilEmpresarialDTO toDTO(PerfilEmpresarial entity) {
        if (entity == null) return null;

        PerfilEmpresarialDTO dto = new PerfilEmpresarialDTO();
        dto.setId(entity.getId());
        dto.setNombreEmpresa(entity.getNombreEmpresa());
        dto.setCuit(entity.getCuit());
        dto.setDescripcion(entity.getDescripcion());
        dto.setSitioWeb(entity.getSitioWeb());
        dto.setUrlLogo(entity.getUrlLogo());
        dto.setVerificado(entity.isVerificado());
        dto.setEmpleador(usuarioResumenMapper.toDTO(entity.getEmpleador()));
        dto.setSupervisadoPor(usuarioResumenMapper.toDTO(entity.getSupervisadoPor()));
        return dto;
    }

    public PerfilEmpresarial toEntity(PerfilEmpresarialDTO dto) {
        if (dto == null) return null;

        PerfilEmpresarial entity = new PerfilEmpresarial();
        entity.setId(dto.getId());
        entity.setNombreEmpresa(dto.getNombreEmpresa());
        entity.setCuit(dto.getCuit());
        entity.setDescripcion(dto.getDescripcion());
        entity.setSitioWeb(dto.getSitioWeb());
        entity.setUrlLogo(dto.getUrlLogo());
        return entity;
    }
}
