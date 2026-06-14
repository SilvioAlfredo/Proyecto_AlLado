package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.dto.PostulacionResumenDTO;
import com.al_lado.al_lado_project.dto.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.Empleador;
import com.al_lado.al_lado_project.entity.OportunidadLaboral;
import com.al_lado.al_lado_project.repository.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.service.OportunidadLaboralService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OportunidadLaboralServiceImpl implements OportunidadLaboralService {

    private final OportunidadLaboralRepository repository;
    private final EmpleadorRepository empleadorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OportunidadLaboralDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OportunidadLaboralDTO obtenerPorId(Long id) {
        OportunidadLaboral oportunidad = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada con ID: " + id));
        return convertToDto(oportunidad);
    }

    @Override
    @Transactional
    public OportunidadLaboralDTO guardar(OportunidadLaboralDTO dto) {
        if (dto.getAutor() == null || dto.getAutor().getId() == null) {
            throw new RuntimeException("El autor (Empleador) es obligatorio para crear la oportunidad.");
        }

        Long autorId = dto.getAutor().getId();
        Empleador empleador = empleadorRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Empleador no encontrado con ID: " + autorId));

        OportunidadLaboral oportunidad = dto.getId() != null ?
                repository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Oportunidad no encontrada"))
                : new OportunidadLaboral();

        oportunidad.setTitulo(dto.getTitulo());
        oportunidad.setDescripcion(dto.getDescripcion());
        oportunidad.setImagen(dto.getImagen());
        oportunidad.setAutor(empleador);

        if (dto.getId() == null) {
            oportunidad.setFechaPublicacion(new Date());
            oportunidad.setEstado("ACTIVA");
        } else {
            oportunidad.setEstado(dto.getEstado());
        }

        OportunidadLaboral guardada = repository.save(oportunidad);
        return convertToDto(guardada);
    }

    @Override
    @Transactional
    public OportunidadLaboralDTO cerrarOportunidad(Long id) {
        OportunidadLaboral oportunidad = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada con ID: " + id));

        oportunidad.setEstado("CERRADA");
        oportunidad.setFechaCierre(new Date());

        OportunidadLaboral actualizada = repository.save(oportunidad);
        return convertToDto(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Oportunidad no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }

    private OportunidadLaboralDTO convertToDto(OportunidadLaboral entity) {
        OportunidadLaboralDTO dto = new OportunidadLaboralDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setImagen(entity.getImagen());

        if (entity.getAutor() != null) {
            UsuarioResumenDTO autorResumen = new UsuarioResumenDTO();
            autorResumen.setId(entity.getAutor().getId());
            autorResumen.setNombre(entity.getAutor().getNombre());
            autorResumen.setEmail(entity.getAutor().getEmail());
            dto.setAutor(autorResumen);
        }

        dto.setEstado(entity.getEstado());
        dto.setFechaPublicacion(entity.getFechaPublicacion());
        dto.setFechaCierre(entity.getFechaCierre());

        if (entity.getPostulaciones() != null) {
            List<PostulacionResumenDTO> resumenes = entity.getPostulaciones().stream().map(postulacion -> {
                PostulacionResumenDTO resumen = new PostulacionResumenDTO();
                resumen.setId(postulacion.getId());
                resumen.setPresupuesto(postulacion.getPresupuesto());
                resumen.setDisponibilidad(postulacion.getDisponibilidad());
                resumen.setEstado(postulacion.getEstado());

                if (postulacion.getAutor() != null) {
                    UsuarioResumenDTO autorPostulante = new UsuarioResumenDTO();
                    autorPostulante.setId(postulacion.getAutor().getId());
                    autorPostulante.setNombre(postulacion.getAutor().getNombre());
                    autorPostulante.setEmail(postulacion.getAutor().getEmail());
                    resumen.setAutor(autorPostulante);
                }
                return resumen;
            }).collect(Collectors.toList());
            dto.setPostulacionesRecibidas(resumenes);
        }
        return dto;
    }
}