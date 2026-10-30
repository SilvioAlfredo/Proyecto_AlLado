package com.al_lado.al_lado_project.mappers.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Convierte ofertas y sus datos relacionados entre entidad y DTO.
 *
 * <p>Al DTO agrega autor, etiquetas y postulaciones. Al crear una entidad convierte las
 * etiquetas, pero el servicio debe resolverlas contra el repositorio y asignar el autor. No
 * copia fecha de creación ni postulaciones desde el DTO.</p>
 */
@Component
@RequiredArgsConstructor
public class OportunidadLaboralMapper extends PostBaseMapper {

    private final EtiquetaMapper etiquetaMapper;
    private final PostulacionResumenMapper postulacionResumenMapper;
    private final UsuarioResumenMapper usuarioResumenMapper;

    /**
     * Copia la oferta, el autor resumido, las etiquetas y las postulaciones.
     *
     * <p>Las relaciones sin inicializar necesitan una sesión abierta y pueden generar consultas
     * adicionales. En un listado esto puede causar N+1.</p>
     *
     * @param entity entidad a convertir, o {@code null}
     */
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
        dto.setLatitud(entity.getLatitud());
        dto.setLongitud(entity.getLongitud());
        dto.setDiaSemanaRequerido(entity.getDiaSemanaRequerido());
        dto.setHoraInicioRequerida(entity.getHoraInicioRequerida());
        dto.setHoraFinRequerida(entity.getHoraFinRequerida());
        dto.setActiva(entity.isActiva());
        dto.setDestacada(entity.isDestacada());

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

        dto.setPostulacionesCount(entity.getPostulaciones() == null
                ? 0 : entity.getPostulaciones().size());

        return dto;
    }

    /**
     * Proyección pública de la oferta (AL-SEC-006).
     *
     * <p>El contenido de la oferta es público, pero el autor sólo se identifica con
     * id y nombre (sin email ni teléfono) y el detalle de los postulantes queda
     * fuera: sólo se informa cuántos hay, en {@code postulacionesCount}.</p>
     *
     * @param entity entidad a convertir, o {@code null}
     * @return DTO con la proyección pública, o {@code null}
     */
    public OportunidadLaboralDTO toDTOPublic(OportunidadLaboral entity) {
        if (entity == null) return null;

        OportunidadLaboralDTO dto = new OportunidadLaboralDTO();
        mapBaseToDTO(entity, dto);

        if (entity.getAutor() != null) {
            dto.setAutor(usuarioResumenMapper.toDTOPublic(entity.getAutor()));
        }

        dto.setUbicacion(entity.getUbicacion());
        dto.setModalidad(entity.getModalidad());
        dto.setRangoSalarial(entity.getRangoSalarial());
        dto.setActiva(entity.isActiva());
        dto.setDestacada(entity.isDestacada());

        if (entity.getEtiquetas() != null) {
            dto.setEtiquetas(entity.getEtiquetas().stream()
                    .map(etiquetaMapper::toDTO)
                    .collect(Collectors.toSet()));
        }

        dto.setPostulacionesCount(entity.getPostulaciones() == null
                ? 0 : entity.getPostulaciones().size());

        return dto;
    }

    /**
     * Crea una entidad con los datos del DTO y sus etiquetas.
     *
     * <p>Las etiquetas son instancias nuevas, no entidades obtenidas del repositorio. El
     * servicio debe resolverlas antes de guardar para evitar duplicados. No asigna autor, fecha
     * de creación ni postulaciones.</p>
     */
    public OportunidadLaboral toEntity(OportunidadLaboralDTO dto) {
        if (dto == null) return null;

        OportunidadLaboral entity = new OportunidadLaboral();
        mapBaseToEntity(dto, entity);

        entity.setUbicacion(dto.getUbicacion());
        entity.setModalidad(dto.getModalidad());
        entity.setRangoSalarial(dto.getRangoSalarial());
        entity.setLatitud(dto.getLatitud());
        entity.setLongitud(dto.getLongitud());
        entity.setDiaSemanaRequerido(dto.getDiaSemanaRequerido());
        entity.setHoraInicioRequerida(dto.getHoraInicioRequerida());
        entity.setHoraFinRequerida(dto.getHoraFinRequerida());
        entity.setActiva(dto.isActiva());
        entity.setDestacada(dto.isDestacada());

        if (dto.getEtiquetas() != null) {
            entity.setEtiquetas(dto.getEtiquetas().stream()
                    .map(etiquetaMapper::toEntity)
                    .collect(Collectors.toSet()));
        }

        return entity;
    }
}