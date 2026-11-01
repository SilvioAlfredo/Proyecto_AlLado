package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.ContratoDTO;
import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionMapper;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.trabajo.ContratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del historial de contratos (HU-08).
 *
 * <p>Se basa en la entidad {@link Postulacion}, que ya modela el ciclo de vida
 * de una contratación con estados {@code PENDIENTE}, {@code SELECCIONADA},
 * {@code COMPLETADA}, {@code RECHAZADA} y {@code RETIRADA}. Los estados de
 * ejecución (todos menos {@code PENDIENTE}) se consideran "contratos" a efectos
 * del historial.
 */
@Service
@RequiredArgsConstructor
public class ContratoServiceImpl implements ContratoService {

    private static final String ESTADO_PENDIENTE = Postulacion.ESTADO_PENDIENTE;

    private final PostulacionRepository repository;
    private final TrabajadorRepository trabajadorRepository;
    private final PostulacionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ContratoDTO> historialEmpleador() {
        Usuario actual = SecurityUtils.currentUser();
        if (actual == null) {
            throw new AccessDeniedException("No hay sesión de usuario autenticada.");
        }
        if (!(actual instanceof Empleador)) {
            throw new AccessDeniedException("Solo un empleador puede consultar su historial de contratos.");
        }

        List<Postulacion> postulaciones = repository.findByOportunidadLaboralAutorId(actual.getId());
        return postulaciones.stream()
                .map(this::toContratoDTO)
                .sorted((a, b) -> {
                    java.time.LocalDateTime fa = a.getFechaCreacion();
                    java.time.LocalDateTime fb = b.getFechaCreacion();
                    if (fa == null && fb == null) return 0;
                    if (fa == null) return 1;
                    if (fb == null) return -1;
                    return fb.compareTo(fa);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratoDTO> historialTrabajador(Long trabajadorId) {
        SecurityUtils.requireSelfOrAdmin(trabajadorId);

        trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + trabajadorId));

        List<Postulacion> postulaciones = repository.findByAutorId(trabajadorId).stream()
                .filter(p -> !ESTADO_PENDIENTE.equalsIgnoreCase(p.getEstado()))
                .collect(Collectors.toList());

        return postulaciones.stream()
                .map(this::toContratoDTO)
                .sorted((a, b) -> {
                    java.time.LocalDateTime fa = a.getFechaCreacion();
                    java.time.LocalDateTime fb = b.getFechaCreacion();
                    if (fa == null && fb == null) return 0;
                    if (fa == null) return 1;
                    if (fb == null) return -1;
                    return fb.compareTo(fa);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ContratoDTO obtenerPorId(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado con ID: " + id));

        exigirAlcanceDeLectura(postulacion);
        return toContratoDTO(postulacion);
    }

    private void exigirAlcanceDeLectura(Postulacion postulacion) {
        if (SecurityUtils.isAdmin()) {
            return;
        }

        Long actual = SecurityUtils.currentUserId();
        if (actual == null) {
            throw new AccessDeniedException("No tiene permisos para consultar este contrato.");
        }

        Long autorId = postulacion.getAutor() != null ? postulacion.getAutor().getId() : null;
        Long empleadorId = postulacion.getOportunidadLaboral() != null
                && postulacion.getOportunidadLaboral().getAutor() != null
                ? postulacion.getOportunidadLaboral().getAutor().getId()
                : null;

        if (actual.equals(autorId) || actual.equals(empleadorId)) {
            return;
        }
        throw new AccessDeniedException("No tiene permisos para consultar este contrato.");
    }

    private ContratoDTO toContratoDTO(Postulacion p) {
        ContratoDTO dto = new ContratoDTO();
        dto.setId(p.getId());
        dto.setEstado(p.getEstado());
        dto.setRemuneracionPretendida(p.getRemuneracionPretendida());
        dto.setFechaCreacion(p.getFechaCreacion());

        if (p.getAutor() != null) {
            dto.setTrabajador(mapToResumen(p.getAutor()));
        }

        if (p.getOportunidadLaboral() != null) {
            dto.setOportunidad(mapToOportunidadResumen(p.getOportunidadLaboral()));
            if (p.getOportunidadLaboral().getAutor() != null) {
                dto.setEmpleador(mapToResumen(p.getOportunidadLaboral().getAutor()));
            }
        }

        return dto;
    }

    private UsuarioResumenDTO mapToResumen(Usuario u) {
        UsuarioResumenDTO dto = new UsuarioResumenDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        return dto;
    }

    private OportunidadLaboralResumenDTO mapToOportunidadResumen(OportunidadLaboral o) {
        OportunidadLaboralResumenDTO dto = new OportunidadLaboralResumenDTO();
        dto.setId(o.getId());
        dto.setTitulo(o.getTitulo());
        dto.setContenido(o.getContenido());
        dto.setUbicacion(o.getUbicacion());
        dto.setModalidad(o.getModalidad());
        dto.setRangoSalarial(o.getRangoSalarial());
        dto.setActiva(o.isActiva());
        dto.setFechaCreacion(o.getFechaCreacion());
        return dto;
    }
}
