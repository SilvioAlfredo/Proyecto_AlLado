package com.al_lado.al_lado_project.service.impl;

import com.al_lado.al_lado_project.dto.PostulacionRequestDTO;
import com.al_lado.al_lado_project.dto.PostulacionResponseDTO;
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
    public List<PostulacionResponseDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PostulacionResponseDTO obtenerPorId(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));
        return convertToResponseDto(postulacion);
    }

    @Override
    @Transactional
    public PostulacionResponseDTO crearPostulacion(PostulacionRequestDTO requestDTO) {
        Trabajador trabajador = trabajadorRepository.findById(requestDTO.getTrabajadorId())
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado con ID: " + requestDTO.getTrabajadorId()));

        OportunidadLaboral oportunidad = oportunidadRepository.findById(requestDTO.getOportunidadId())
                .orElseThrow(() -> new RuntimeException("Oportunidad laboral no encontrada con ID: " + requestDTO.getOportunidadId()));

        if ("CERRADA".equalsIgnoreCase(oportunidad.getEstado())) {
            throw new RuntimeException("No se permiten postulaciones a una oportunidad cerrada.");
        }

        Postulacion postulacion = new Postulacion();
        postulacion.setTitulo(requestDTO.getTitulo());
        postulacion.setDescripcion(requestDTO.getDescripcion());
        postulacion.setPresupuesto(requestDTO.getPresupuesto());
        postulacion.setDisponibilidad(requestDTO.getDisponibilidad());
        postulacion.setAutor(trabajador);
        postulacion.setOportunidad(oportunidad);
        postulacion.setEstado("PENDIENTE");

        Postulacion guardada = repository.save(postulacion);
        return convertToResponseDto(guardada);
    }

    @Override
    @Transactional
    public PostulacionResponseDTO seleccionarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));

        postulacion.setEstado("SELECCIONADA");
        Postulacion actualizada = repository.save(postulacion);
        return convertToResponseDto(actualizada);
    }

    @Override
    @Transactional
    public PostulacionResponseDTO rechazarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));

        postulacion.setEstado("RECHAZADA");
        Postulacion actualizada = repository.save(postulacion);
        return convertToResponseDto(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Postulación no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }

    private PostulacionResponseDTO convertToResponseDto(Postulacion entity) {
        PostulacionResponseDTO dto = new PostulacionResponseDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setPresupuesto(entity.getPresupuesto());
        dto.setDisponibilidad(entity.getDisponibilidad());
        dto.setEstado(entity.getEstado());
        if (entity.getAutor() != null) {
            dto.setTrabajadorId(entity.getAutor().getId());
        }
        if (entity.getOportunidad() != null) {
            dto.setOportunidadId(entity.getOportunidad().getId());
        }
        return dto;
    }
}