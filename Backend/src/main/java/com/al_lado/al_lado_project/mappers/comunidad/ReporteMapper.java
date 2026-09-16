package com.al_lado.al_lado_project.mappers.comunidad;

import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;

import com.al_lado.al_lado_project.dto.comunidad.ReporteDTO;
import com.al_lado.al_lado_project.entity.comunidad.Reporte;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReporteMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public ReporteDTO toDTO(Reporte entity) {
        if (entity == null) return null;

        ReporteDTO dto = new ReporteDTO();
        dto.setId(entity.getId());
        dto.setMotivo(entity.getMotivo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setEstado(entity.getEstado());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setReportante(usuarioResumenMapper.toDTO(entity.getReportante()));
        dto.setReportado(usuarioResumenMapper.toDTO(entity.getReportado()));
        dto.setModerador(usuarioResumenMapper.toDTO(entity.getModerador()));
        return dto;
    }

    public Reporte toEntity(ReporteDTO dto) {
        if (dto == null) return null;

        Reporte entity = new Reporte();
        entity.setId(dto.getId());
        entity.setMotivo(dto.getMotivo());
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }
}
