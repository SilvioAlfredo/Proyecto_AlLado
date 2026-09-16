package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionMapper;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.service.trabajo.PostulacionService;
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
    private final PostulacionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<PostulacionDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PostulacionDTO obtenerPorId(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulación no encontrada con ID: " + id));
        return mapper.toDTO(postulacion);
    }

    @Override
    @Transactional
    public PostulacionDTO crearPostulacion(PostulacionDTO dto) {
        if (dto.getAutor() == null || dto.getAutor().getId() == null) {
            throw new BusinessRuleException("El autor (Trabajador) es obligatorio.");
        }
        if (dto.getOportunidadLaboral() == null || dto.getOportunidadLaboral().getId() == null) {
            throw new BusinessRuleException("La oportunidad laboral es obligatoria.");
        }

        Long trabajadorId = dto.getAutor().getId();
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + trabajadorId));

        Long oportunidadId = dto.getOportunidadLaboral().getId();
        OportunidadLaboral oportunidad = oportunidadRepository.findById(oportunidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidad laboral no encontrada con ID: " + oportunidadId));

        if (!oportunidad.isActiva()) {
            throw new BusinessRuleException("No se permiten postulaciones a una oportunidad laboral cerrada.");
        }

        if (repository.existsByOportunidadLaboralIdAndAutorId(oportunidadId, trabajadorId)) {
            throw new DuplicateResourceException("El trabajador ya se encuentra postulado a esta oportunidad laboral.");
        }

        Postulacion postulacion = mapper.toEntity(dto);
        postulacion.setAutor(trabajador);
        postulacion.setOportunidadLaboral(oportunidad);
        postulacion.setEstado("PENDIENTE");

        return mapper.toDTO(repository.save(postulacion));
    }

    @Override
    @Transactional
    public PostulacionDTO seleccionarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulación no encontrada con ID: " + id));

        postulacion.setEstado("SELECCIONADA");
        return mapper.toDTO(repository.save(postulacion));
    }

    @Override
    @Transactional
    public PostulacionDTO rechazarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulación no encontrada con ID: " + id));

        postulacion.setEstado("RECHAZADA");
        return mapper.toDTO(repository.save(postulacion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Postulación no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}