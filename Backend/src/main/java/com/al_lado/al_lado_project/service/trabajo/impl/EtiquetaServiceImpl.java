package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.EtiquetaDTO;
import com.al_lado.al_lado_project.entity.trabajo.Etiqueta;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.trabajo.EtiquetaMapper;
import com.al_lado.al_lado_project.repository.trabajo.EtiquetaRepository;
import com.al_lado.al_lado_project.service.trabajo.EtiquetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EtiquetaServiceImpl implements EtiquetaService {

    private final EtiquetaRepository repository;
    private final EtiquetaMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<EtiquetaDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EtiquetaDTO obtenerPorId(Long id) {
        Etiqueta etiqueta = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + id));
        return mapper.toDTO(etiqueta);
    }

    @Override
    @Transactional
    public EtiquetaDTO obtenerOCrearPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessRuleException("El nombre de la etiqueta es obligatorio.");
        }

        String nombreNormalizado = nombre.trim();
        try {
            Etiqueta etiqueta = repository.findByNombreIgnoreCase(nombreNormalizado)
                    .orElseGet(() -> repository.save(new Etiqueta(nombreNormalizado)));
            return mapper.toDTO(etiqueta);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Alta concurrente con el mismo nombre: re-leer en vez de duplicar.
            Etiqueta etiqueta = repository.findByNombreIgnoreCase(nombreNormalizado)
                    .orElseThrow(() -> e);
            return mapper.toDTO(etiqueta);
        }
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Etiqueta no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}