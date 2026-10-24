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

    /**
     * Proyección pública del perfil empresarial (AL-SEC-006).
     *
     * <p>Es la vitrina del empleador, así que conserva nombre de la empresa,
     * descripción, sitio web, logo y verificación. El CUIT y las relaciones
     * (empleador, supervisor) son datos privados: no se exponen.</p>
     *
     * @param entity entidad a convertir, o {@code null}
     * @return DTO con la proyección pública, o {@code null}
     */
    public PerfilEmpresarialDTO toDTOPublic(PerfilEmpresarial entity) {
        if (entity == null) return null;

        PerfilEmpresarialDTO dto = new PerfilEmpresarialDTO();
        dto.setId(entity.getId());
        dto.setNombreEmpresa(entity.getNombreEmpresa());
        dto.setDescripcion(entity.getDescripcion());
        dto.setSitioWeb(entity.getSitioWeb());
        dto.setUrlLogo(entity.getUrlLogo());
        dto.setVerificado(entity.isVerificado());
        dto.setEmpleador(usuarioResumenMapper.toDTOPublic(entity.getEmpleador()));
        return dto;
    }

    public PerfilEmpresarial toEntity(PerfilEmpresarialDTO dto) {
        if (dto == null) return null;

        PerfilEmpresarial entity = new PerfilEmpresarial();
        // MEJ-003 / AL-SEC-008: el ID que manda el cliente no se copia en un alta.
        // Sólo la base de datos asigna IDs, de modo que ninguna petición puede
        // apuntar el alta a un registro existente y sobrescribirlo.
        entity.setId(null);
        entity.setNombreEmpresa(dto.getNombreEmpresa());
        entity.setCuit(dto.getCuit());
        entity.setDescripcion(dto.getDescripcion());
        entity.setSitioWeb(dto.getSitioWeb());
        entity.setUrlLogo(dto.getUrlLogo());
        return entity;
    }
}
