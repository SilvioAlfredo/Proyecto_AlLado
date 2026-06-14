package com.al_lado.al_lado_project.service.impl;

import com.al_lado.al_lado_project.dto.OportunidadLaboralRequestDTO;
import com.al_lado.al_lado_project.dto.OportunidadLaboralResponseDTO;
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
    public List<OportunidadLaboralResponseDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OportunidadLaboralResponseDTO obtenerPorId(Long id) {
        OportunidadLaboral oportunidad = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada con ID: " + id));
        return convertToResponseDto(oportunidad);
    }

    @Override
    @Transactional
    public OportunidadLaboralResponseDTO guardar(OportunidadLaboralRequestDTO requestDTO) {
        Empleador empleador = empleadorRepository.findById(requestDTO.getEmpleadorId())
                .orElseThrow(() -> new RuntimeException("Empleador no encontrado con ID: " + requestDTO.getEmpleadorId()));

        OportunidadLaboral oportunidad = new OportunidadLaboral();
        oportunidad.setTitulo(requestDTO.getTitulo());
        oportunidad.setDescripcion(requestDTO.getDescripcion());
        oportunidad.setImagen(requestDTO.getImagen());
        oportunidad.setAutor(empleador);
        oportunidad.setFechaPublicacion(new Date());
        oportunidad.setEstado("ACTIVA");

        OportunidadLaboral guardada = repository.save(oportunidad);
        return convertToResponseDto(guardada);
    }

    @Override
    @Transactional
    public OportunidadLaboralResponseDTO cerrarOportunidad(Long id) {
        OportunidadLaboral oportunidad = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada con ID: " + id));

        oportunidad.setEstado("CERRADA");
        oportunidad.setFechaCierre(new Date());

        OportunidadLaboral actualizada = repository.save(oportunidad);
        return convertToResponseDto(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Oportunidad no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }

    private OportunidadLaboralResponseDTO convertToResponseDto(OportunidadLaboral entity) {
        OportunidadLaboralResponseDTO dto = new OportunidadLaboralResponseDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setImagen(entity.getImagen());
        dto.setEstado(entity.getEstado());
        dto.setFechaPublicacion(entity.getFechaPublicacion());
        if (entity.getAutor() != null) {
            dto.setEmpleadorId(entity.getAutor().getId());
        }
        return dto;
    }
}