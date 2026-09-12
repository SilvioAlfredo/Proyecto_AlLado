package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.mappers.OportunidadLaboralMapper;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
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
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada con ID: " + id));
        return mapper.toDTO(oportunidad);
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
                : mapper.toEntity(dto);

        oportunidad.setTitulo(dto.getTitulo());
        oportunidad.setContenido(dto.getTitulo()); // PostBase utiliza 'contenido'
        oportunidad.setAutor(empleador);

        if (dto.getId() == null) {
            oportunidad.setActiva(true);
        } else {
            oportunidad.setActiva(dto.getEstado() == null || !"CERRADA".equalsIgnoreCase(dto.getEstado()));
        }

        return mapper.toDTO(repository.save(oportunidad));
    }

    @Override
    @Transactional
    public OportunidadLaboralDTO cerrarOportunidad(Long id) {
        OportunidadLaboral oportunidad = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada con ID: " + id));

        oportunidad.setActiva(false);

        return mapper.toDTO(repository.save(oportunidad));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Oportunidad no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}