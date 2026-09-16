package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.ValoracionMapper;
import com.al_lado.al_lado_project.repository.comunidad.ValoracionRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.service.comunidad.ValoracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValoracionServiceImpl implements ValoracionService {

    private final ValoracionRepository repository;
    private final EmpleadorRepository empleadorRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final ValoracionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ValoracionDTO> obtenerPorTrabajador(Long trabajadorId) {
        return repository.findByTrabajadorId(trabajadorId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValoracionDTO> obtenerPorAutor(Long autorId) {
        return repository.findByAutorId(autorId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerPromedioPorTrabajador(Long trabajadorId) {
        Double promedio = repository.calcularPromedioPuntuacionPorTrabajadorId(trabajadorId);
        return promedio != null ? promedio : 0.0;
    }

    @Override
    @Transactional
    public ValoracionDTO crear(ValoracionDTO dto) {
        if (dto.getAutor() == null || dto.getAutor().getId() == null) {
            throw new BusinessRuleException("El autor (Empleador) es obligatorio.");
        }
        if (dto.getTrabajador() == null || dto.getTrabajador().getId() == null) {
            throw new BusinessRuleException("El trabajador valorado es obligatorio.");
        }
        if (dto.getPuntuacion() == null || dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5) {
            throw new BusinessRuleException("La puntuación debe estar entre 1 y 5.");
        }

        Long autorId = dto.getAutor().getId();
        Long trabajadorId = dto.getTrabajador().getId();

        if (repository.existsByAutorIdAndTrabajadorId(autorId, trabajadorId)) {
            throw new DuplicateResourceException("Ya existe una valoración de este empleador a este trabajador.");
        }

        Empleador autor = empleadorRepository.findById(autorId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleador no encontrado con ID: " + autorId));
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + trabajadorId));

        Valoracion valoracion = mapper.toEntity(dto);
        valoracion.setAutor(autor);
        valoracion.setTrabajador(trabajador);

        return mapper.toDTO(repository.save(valoracion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Valoración no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}