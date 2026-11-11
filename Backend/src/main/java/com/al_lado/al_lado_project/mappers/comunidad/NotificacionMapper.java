package com.al_lado.al_lado_project.mappers.comunidad;

import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;

import com.al_lado.al_lado_project.dto.comunidad.NotificacionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Notificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public NotificacionDTO toDTO(Notificacion entity) {
        if (entity == null) return null;

        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(entity.getId());
        dto.setDestinatario(usuarioResumenMapper.toDTO(entity.getDestinatario()));
        dto.setTitulo(entity.getTitulo());
        dto.setMensaje(entity.getMensaje());
        dto.setTipo(entity.getTipo());
        dto.setReferenciaId(entity.getReferenciaId());
        dto.setLeida(entity.isLeida());
        dto.setFechaCreacion(entity.getFechaCreacion());
        return dto;
    }

    // El destinatario se resuelve en NotificacionServiceImpl por ID.
    public Notificacion toEntity(NotificacionDTO dto) {
        if (dto == null) return null;

        Notificacion entity = new Notificacion();
        entity.setId(dto.getId());
        entity.setTitulo(dto.getTitulo());
        entity.setMensaje(dto.getMensaje());
        entity.setTipo(dto.getTipo() != null ? dto.getTipo() : Notificacion.TIPO_SISTEMA);
        entity.setReferenciaId(dto.getReferenciaId());
        entity.setLeida(dto.isLeida());
        return entity;
    }
}
