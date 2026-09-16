package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.PublicidadDTO;
import com.al_lado.al_lado_project.entity.comunidad.Publicidad;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.PublicidadMapper;
import com.al_lado.al_lado_project.repository.comunidad.PublicidadRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.service.comunidad.PublicidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicidadServiceImpl implements PublicidadService {

    private final PublicidadRepository repository;
    private final EmpleadorRepository empleadorRepository;
    private final PublicidadMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<PublicidadDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PublicidadDTO obtenerPorId(Long id) {
        Publicidad publicidad = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicidad no encontrada con ID: " + id));
        return mapper.toDTO(publicidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicidadDTO> obtenerPorAnunciante(Long anuncianteId) {
        return repository.findByAnuncianteId(anuncianteId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicidadDTO> obtenerVigentes() {
        return repository.buscarPublicidadesVigentes(LocalDate.now()).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PublicidadDTO guardar(PublicidadDTO dto) {
        if (dto.getAnunciante() == null || dto.getAnunciante().getId() == null) {
            throw new BusinessRuleException("El anunciante (Empleador) es obligatorio.");
        }
        if (dto.getFechaInicio() == null || dto.getFechaFin() == null) {
            throw new BusinessRuleException("Las fechas de inicio y fin son obligatorias.");
        }
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new BusinessRuleException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        Long anuncianteId = dto.getAnunciante().getId();
        Empleador anunciante = empleadorRepository.findById(anuncianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleador no encontrado con ID: " + anuncianteId));

        Publicidad publicidad = dto.getId() != null ?
                repository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Publicidad no encontrada"))
                : mapper.toEntity(dto);

        publicidad.setTitulo(dto.getTitulo());
        publicidad.setContenido(dto.getContenido());
        publicidad.setUrlImagen(dto.getUrlImagen());
        publicidad.setUrlDestino(dto.getUrlDestino());
        publicidad.setFechaInicio(dto.getFechaInicio());
        publicidad.setFechaFin(dto.getFechaFin());
        publicidad.setActiva(dto.isActiva());
        publicidad.setAnunciante(anunciante);

        return mapper.toDTO(repository.save(publicidad));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Publicidad no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}