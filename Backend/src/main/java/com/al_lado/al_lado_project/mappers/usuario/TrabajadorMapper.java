package com.al_lado.al_lado_project.mappers.usuario;

import com.al_lado.al_lado_project.dto.usuario.TrabajadorDTO;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.mappers.perfil.HorarioDisponibilidadMapper;
import com.al_lado.al_lado_project.mappers.perfil.PortafolioItemMapper;
import com.al_lado.al_lado_project.mappers.perfil.PreferenciaNotificacionMapper;
import com.al_lado.al_lado_project.mappers.trabajo.EtiquetaMapper;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionResumenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrabajadorMapper {

    private final EtiquetaMapper etiquetaMapper;
    private final PostulacionResumenMapper postulacionResumenMapper;
    private final PortafolioItemMapper portafolioItemMapper;
    private final HorarioDisponibilidadMapper horarioDisponibilidadMapper;
    private final PreferenciaNotificacionMapper preferenciaNotificacionMapper;

    public TrabajadorDTO toDTO(Trabajador entity) {
        if (entity == null) return null;

        TrabajadorDTO dto = new TrabajadorDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());

        if (entity.getEspecialidades() != null) {
            dto.setEspecialidades(entity.getEspecialidades().stream()
                    .map(etiquetaMapper::toDTO)
                    .collect(Collectors.toSet()));
        }

        if (entity.getPostulaciones() != null) {
            dto.setPostulaciones(entity.getPostulaciones().stream()
                    .map(postulacionResumenMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        if (entity.getPortafolioItems() != null) {
            dto.setPortafolioItems(entity.getPortafolioItems().stream()
                    .map(portafolioItemMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        if (entity.getHorarios() != null) {
            dto.setHorarios(entity.getHorarios().stream()
                    .map(horarioDisponibilidadMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        if (entity.getPreferenciaNotificacion() != null) {
            dto.setPreferenciaNotificacion(preferenciaNotificacionMapper.toDTO(entity.getPreferenciaNotificacion()));
        }

        return dto;
    }

    public Trabajador toEntity(TrabajadorDTO dto) {
        if (dto == null) return null;

        Trabajador entity = new Trabajador();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());

        if (dto.getEspecialidades() != null) {
            entity.setEspecialidades(dto.getEspecialidades().stream()
                    .map(etiquetaMapper::toEntity)
                    .collect(Collectors.toSet()));
        }

        return entity;
    }
}