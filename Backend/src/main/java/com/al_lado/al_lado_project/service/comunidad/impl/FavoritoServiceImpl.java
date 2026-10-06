package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.FavoritoDTO;
import com.al_lado.al_lado_project.entity.comunidad.Favorito;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.FavoritoMapper;
import com.al_lado.al_lado_project.repository.comunidad.FavoritoRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.FavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository repository;
    private final EmpleadorRepository empleadorRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final FavoritoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<FavoritoDTO> obtenerPorEmpleador(Long empleadorId) {
        SecurityUtils.requireSelfOrAdmin(empleadorId);
        return repository.findByEmpleadorId(empleadorId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FavoritoDTO agregar(Long empleadorId, Long trabajadorId) {
        SecurityUtils.requireSelfOrAdmin(empleadorId);
        if (repository.existsByEmpleadorIdAndTrabajadorId(empleadorId, trabajadorId)) {
            throw new DuplicateResourceException("Este trabajador ya está en la lista de favoritos.");
        }

        Empleador empleador = empleadorRepository.findById(empleadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleador no encontrado con ID: " + empleadorId));
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + trabajadorId));

        Favorito favorito = new Favorito(empleador, trabajador);
        return mapper.toDTO(repository.save(favorito));
    }

    @Override
    @Transactional
    public void quitar(Long empleadorId, Long trabajadorId) {
        SecurityUtils.requireSelfOrAdmin(empleadorId);
        if (!repository.existsByEmpleadorIdAndTrabajadorId(empleadorId, trabajadorId)) {
            throw new ResourceNotFoundException("El trabajador no se encuentra en la lista de favoritos.");
        }
        repository.deleteByEmpleadorIdAndTrabajadorId(empleadorId, trabajadorId);
    }
}