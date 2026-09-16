package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.usuario.TrabajadorDTO;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.usuario.TrabajadorMapper;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.service.usuario.TrabajadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements TrabajadorService {

    private final TrabajadorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final TrabajadorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TrabajadorDTO obtenerPorId(Long id) {
        Trabajador trabajador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + id));
        return mapper.toDTO(trabajador);
    }

    @Override
    @Transactional
    public TrabajadorDTO guardar(TrabajadorDTO dto) {
        Trabajador trabajador;

        if (dto.getId() == null) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("El email ya se encuentra registrado.");
            }
            trabajador = mapper.toEntity(dto);
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                trabajador.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        } else {
            trabajador = repository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + dto.getId()));

            if (!trabajador.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("El email ya se encuentra registrado por otro usuario.");
            }

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                trabajador.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        }

        trabajador.setNombre(dto.getNombre());
        trabajador.setEmail(dto.getEmail());

        return mapper.toDTO(repository.save(trabajador));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Trabajador no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}