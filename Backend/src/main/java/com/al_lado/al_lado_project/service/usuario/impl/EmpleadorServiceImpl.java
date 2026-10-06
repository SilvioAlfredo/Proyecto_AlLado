package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.usuario.EmpleadorDTO;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.usuario.EmpleadorMapper;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.usuario.EmpleadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpleadorServiceImpl implements EmpleadorService {

    private final EmpleadorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadorDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadorDTO obtenerPorId(Long id) {
        Empleador empleador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleador no encontrado con ID: " + id));
        return mapper.toDTO(empleador);
    }

    @Override
    @Transactional
    public EmpleadorDTO guardar(EmpleadorDTO dto) {
        Empleador empleador;

        if (dto.getId() == null) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("El email ya se encuentra registrado.");
            }
            empleador = mapper.toEntity(dto);
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                empleador.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        } else {
            SecurityUtils.requireSelfOrAdmin(dto.getId());
            empleador = repository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empleador no encontrado con ID: " + dto.getId()));

            if (!Objects.equals(empleador.getEmail(), dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("El email ya se encuentra registrado por otro usuario.");
            }

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                empleador.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        }

        empleador.setNombre(dto.getNombre());
        empleador.setEmail(dto.getEmail());
        empleador.setTelefono(dto.getTelefono());

        return mapper.toDTO(repository.save(empleador));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        SecurityUtils.requireSelfOrAdmin(id);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Empleador no encontrado con ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new BusinessRuleException("No se puede eliminar el empleador porque tiene elementos asociados.");
        }
    }
}