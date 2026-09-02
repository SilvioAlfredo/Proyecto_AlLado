package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.EmpleadorDTO;
import com.al_lado.al_lado_project.entity.Empleador;
import com.al_lado.al_lado_project.mappers.EmpleadorMapper;
import com.al_lado.al_lado_project.repository.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.UsuarioRepository;
import com.al_lado.al_lado_project.service.EmpleadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpleadorServiceImpl implements EmpleadorService {

    private final EmpleadorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadorMapper mapper;

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
                .orElseThrow(() -> new RuntimeException("Empleador no encontrado con ID: " + id));
        return mapper.toDTO(empleador);
    }

    @Override
    @Transactional
    public EmpleadorDTO guardar(EmpleadorDTO dto) {
        Empleador empleador;

        if (dto.getId() == null) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("El email ya está registrado.");
            }
            empleador = mapper.toEntity(dto);
        } else {
            empleador = repository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Empleador no encontrado"));

            if (!empleador.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("El email ya está registrado por otro usuario.");
            }

            empleador.setNombre(dto.getNombre());
            empleador.setEmail(dto.getEmail());

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                empleador.setPassword(dto.getPassword());
            }
        }

        return mapper.toDTO(repository.save(empleador));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Empleador no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}