package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.dto.PostulacionDTO;
import com.al_lado.al_lado_project.dto.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.Postulacion;
import com.al_lado.al_lado_project.entity.Trabajador;
import com.al_lado.al_lado_project.repository.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.PostulacionRepository;
import com.al_lado.al_lado_project.repository.TrabajadorRepository;
import com.al_lado.al_lado_project.service.PostulacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostulacionServiceImpl implements PostulacionService {

    private final PostulacionRepository repository;
    private final TrabajadorRepository trabajadorRepository;
    private final OportunidadLaboralRepository oportunidadRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PostulacionDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PostulacionDTO obtenerPorId(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));
        return convertToDto(postulacion);
    }

    @Override
    @Transactional
    public PostulacionDTO crearPostulacion(PostulacionDTO dto) {
        if (dto.getAutor() == null || dto.getAutor().getId() == null) {
            throw new RuntimeException("El autor (Trabajador) es obligatorio.");
        }
        if (dto.getOportunidad() == null || dto.getOportunidad().getId() == null) {
            throw new RuntimeException("La oportunidad laboral es obligatoria.");
        }

        Long trabajadorId = dto.getAutor().getId();
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado con ID: " + trabajadorId));

        Long oportunidadId = dto.getOportunidad().getId();
        OportunidadLaboral oportunidad = oportunidadRepository.findById(oportunidadId)
                .orElseThrow(() -> new RuntimeException("Oportunidad laboral no encontrada con ID: " + oportunidadId));

        if ("CERRADA".equalsIgnoreCase(oportunidad.getEstado())) {
            throw new RuntimeException("No se permiten postulaciones a una oportunidad cerrada.");
        }

        Postulacion postulacion = new Postulacion();
        postulacion.setTitulo(dto.getTitulo());
        postulacion.setDescripcion(dto.getDescripcion());
        postulacion.setImagen(dto.getImagen());
        postulacion.setPresupuesto(dto.getPresupuesto());
        postulacion.setDisponibilidad(dto.getDisponibilidad());
        postulacion.setAutor(trabajador);
        postulacion.setOportunidad(oportunidad);
        postulacion.setEstado("PENDIENTE");

        Postulacion guardada = repository.save(postulacion);
        return convertToDto(guardada);
    }

    @Override
    @Transactional
    public PostulacionDTO seleccionarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));

        postulacion.setEstado("SELECCIONADA");
        Postulacion actualizada = repository.save(postulacion);
        return convertToDto(actualizada);
    }

    @Override
    @Transactional
    public PostulacionDTO rechazarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));

        postulacion.setEstado("RECHAZADA");
        Postulacion actualizada = repository.save(postulacion);
        return convertToDto(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Postulación no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }

    private PostulacionDTO convertToDto(Postulacion entity) {
        PostulacionDTO dto = new PostulacionDTO();
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

        dto.setPresupuesto(entity.getPresupuesto());
        dto.setDisponibilidad(entity.getDisponibilidad());
        dto.setEstado(entity.getEstado());

        if (entity.getOportunidad() != null) {
            OportunidadLaboralResumenDTO opResumen = new OportunidadLaboralResumenDTO();
            opResumen.setId(entity.getOportunidad().getId());
            opResumen.setTitulo(entity.getOportunidad().getTitulo());
            opResumen.setDescripcion(entity.getOportunidad().getDescripcion());
            opResumen.setImagen(entity.getOportunidad().getImagen());
            opResumen.setEstado(entity.getOportunidad().getEstado());
            opResumen.setFechaPublicacion(entity.getOportunidad().getFechaPublicacion());
            opResumen.setFechaCierre(entity.getOportunidad().getFechaCierre());
            dto.setOportunidad(opResumen);
        }

        return dto;
    }
}