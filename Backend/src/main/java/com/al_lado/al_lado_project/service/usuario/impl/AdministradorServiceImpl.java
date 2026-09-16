package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.usuario.AdministradorDTO;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.usuario.AdministradorMapper;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.service.usuario.AdministradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<AdministradorDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdministradorDTO obtenerPorId(Long id) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + id));
        return mapper.toDTO(administrador);
    }

    @Override
    @Transactional
    public AdministradorDTO guardar(AdministradorDTO dto) {
        Administrador administrador;

        if (dto.getId() == null) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("El email ya se encuentra registrado.");
            }
            administrador = mapper.toEntity(dto);
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                administrador.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        } else {
            administrador = repository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + dto.getId()));

            if (!administrador.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("El email ya se encuentra registrado por otro usuario.");
            }

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                administrador.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        }

        administrador.setNombre(dto.getNombre());
        administrador.setEmail(dto.getEmail());
        administrador.setNivelAcceso(dto.getNivelAcceso());

        return mapper.toDTO(repository.save(administrador));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Administrador no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}