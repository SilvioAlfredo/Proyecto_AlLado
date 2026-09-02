package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.Empleador;
import com.al_lado.al_lado_project.entity.OportunidadLaboral;
import com.al_lado.al_lado_project.mappers.OportunidadLaboralMapper;
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
        oportunidad.setDescripcion(dto.getDescripcion());
        oportunidad.setAutor(empleador);

        if (dto.getId() == null) {
            oportunidad.setFechaPublicacion(new Date());
            oportunidad.setEstado("ACTIVA");
        } else {
            oportunidad.setEstado(dto.getEstado());
        }

        return mapper.toDTO(repository.save(oportunidad));
    }

    @Override
    @Transactional
    public OportunidadLaboralDTO cerrarOportunidad(Long id) {
        OportunidadLaboral oportunidad = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada con ID: " + id));

        oportunidad.setEstado("CERRADA");
        oportunidad.setFechaCierre(new Date());

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