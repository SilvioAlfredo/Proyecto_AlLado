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

/**
 * Convierte los datos del trabajador y sus relaciones entre entidad y DTO.
 *
 * <p>El DTO de salida no incluye contraseña. Al crear una entidad, copia la contraseña y
 * convierte las especialidades; el servicio debe codificarla y resolver las etiquetas contra el
 * repositorio.</p>
 */
@Component
@RequiredArgsConstructor
public class TrabajadorMapper {

    private final EtiquetaMapper etiquetaMapper;
    private final PostulacionResumenMapper postulacionResumenMapper;
    private final PortafolioItemMapper portafolioItemMapper;
    private final HorarioDisponibilidadMapper horarioDisponibilidadMapper;
    private final PreferenciaNotificacionMapper preferenciaNotificacionMapper;

    /**
     * Copia el perfil, especialidades, postulaciones, portafolio, horarios y preferencias.
     *
     * <p>Leer relaciones sin inicializar necesita una sesión abierta y puede generar consultas
     * adicionales. En un listado esto puede causar N+1.</p>
     *
     * @param entity entidad a convertir, o {@code null}
     */
    public TrabajadorDTO toDTO(Trabajador entity) {
        if (entity == null) return null;

        TrabajadorDTO dto = new TrabajadorDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setHabilitado(entity.isHabilitado());

        // RF-02, HU-03: campos de perfil trabajador
        dto.setFotografia(entity.getFotografia());
        dto.setBiografia(entity.getBiografia());
        dto.setCertificados(entity.getCertificados());
        dto.setSector(entity.getSector());
        dto.setLatitud(entity.getLatitud());
        dto.setLongitud(entity.getLongitud());

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

    /**
     * Proyección pública del directorio de trabajadores (AL-SEC-006).
     *
     * <p>Conserva lo que el directorio muestra a cualquiera: nombre, estado de la
     * cuenta, ficha de perfil (fotografía, biografía, certificados y sector) y
     * especialidades. No expone el email ni el teléfono (datos de contacto) ni las
     * colecciones privadas: postulaciones, portafolio, horarios y preferencias.</p>
     *
     * @param entity entidad a convertir, o {@code null}
     * @return DTO con la proyección pública, o {@code null}
     */
    public TrabajadorDTO toDTOPublic(Trabajador entity) {
        if (entity == null) return null;

        TrabajadorDTO dto = new TrabajadorDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setHabilitado(entity.isHabilitado());

        // RF-02, HU-03: ficha pública del perfil trabajador
        dto.setFotografia(entity.getFotografia());
        dto.setBiografia(entity.getBiografia());
        dto.setCertificados(entity.getCertificados());
        dto.setSector(entity.getSector());

        if (entity.getEspecialidades() != null) {
            dto.setEspecialidades(entity.getEspecialidades().stream()
                    .map(etiquetaMapper::toDTO)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    /**
     * Crea una entidad con los datos del DTO, su ID y sus especialidades.
     *
     * <p>No busca las etiquetas en la base. El servicio debe resolverlas y codificar la
     * contraseña antes de guardar. Si el ID ya existe, el guardado puede actualizar ese
     * registro.</p>
     */
    public Trabajador toEntity(TrabajadorDTO dto) {
        if (dto == null) return null;

        Trabajador entity = new Trabajador();
        // MEJ-003 / AL-SEC-008: el ID que manda el cliente no se copia en un alta.
        // Sólo la base de datos asigna IDs, de modo que ninguna petición puede
        // apuntar el alta a un registro existente y sobrescribirlo. En edición el
        // servicio recarga la entidad existente y no pasa por aquí.
        entity.setId(null);
        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setTelefono(dto.getTelefono());

        // RF-02, HU-03: campos de perfil trabajador
        entity.setFotografia(dto.getFotografia());
        entity.setBiografia(dto.getBiografia());
        entity.setCertificados(dto.getCertificados());
        entity.setSector(dto.getSector());
        entity.setLatitud(dto.getLatitud());
        entity.setLongitud(dto.getLongitud());

        if (dto.getEspecialidades() != null) {
            entity.setEspecialidades(dto.getEspecialidades().stream()
                    .map(etiquetaMapper::toEntity)
                    .collect(Collectors.toSet()));
        }

        return entity;
    }
}