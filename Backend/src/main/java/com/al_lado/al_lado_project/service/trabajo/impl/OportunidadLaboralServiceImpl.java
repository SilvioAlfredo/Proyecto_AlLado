package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.trabajo.OportunidadLaboralMapper;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.service.trabajo.OportunidadLaboralService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OportunidadLaboralServiceImpl implements OportunidadLaboralService {

    private final OportunidadLaboralRepository repository;
    private final EmpleadorRepository empleadorRepository;
    private final OportunidadLaboralMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<OportunidadLaboralDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OportunidadLaboralDTO obtenerPorId(Long id) {
        OportunidadLaboral oportunidad = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidad no encontrada con ID: " + id));
        return mapper.toDTO(oportunidad);
    }

    @Override
    @Transactional
    public OportunidadLaboralDTO guardar(OportunidadLaboralDTO dto) {
        if (dto.getAutor() == null || dto.getAutor().getId() == null) {
            throw new BusinessRuleException("El autor (Empleador) es obligatorio para crear la oportunidad.");
        }

        Long autorId = dto.getAutor().getId();
        Empleador empleador = empleadorRepository.findById(autorId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleador no encontrado con ID: " + autorId));

        OportunidadLaboral oportunidad = dto.getId() != null ?
                repository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Oportunidad no encontrada con ID: " + dto.getId()))
                : mapper.toEntity(dto);

        oportunidad.setTitulo(dto.getTitulo());
        oportunidad.setContenido(dto.getContenido());
        oportunidad.setUbicacion(dto.getUbicacion());
        oportunidad.setModalidad(dto.getModalidad());
        oportunidad.setRangoSalarial(dto.getRangoSalarial());
        oportunidad.setAutor(empleador);

        if (dto.getId() == null) {
            oportunidad.setActiva(true);
        } else {
            oportunidad.setActiva(dto.isActiva());
        }

        return mapper.toDTO(repository.save(oportunidad));
    }

    @Override
    @Transactional
    public OportunidadLaboralDTO cerrarOportunidad(Long id) {
        OportunidadLaboral oportunidad = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidad no encontrada con ID: " + id));

        oportunidad.setActiva(false);
        return mapper.toDTO(repository.save(oportunidad));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Oportunidad no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}