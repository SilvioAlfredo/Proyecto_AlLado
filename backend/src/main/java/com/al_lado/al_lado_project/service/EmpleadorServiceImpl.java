package com.al_lado.al_lado_project.service.impl;

import com.al_lado.al_lado_project.dto.EmpleadorRequestDTO;
import com.al_lado.al_lado_project.dto.EmpleadorResponseDTO;
import com.al_lado.al_lado_project.entity.Empleador;
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

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadorResponseDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadorResponseDTO obtenerPorId(Long id) {
        Empleador empleador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleador no encontrado con ID: " + id));
        return convertToResponseDto(empleador);
    }

    @Override
    public Empleador guardar(Empleador empleador) {
        return null;
    }

    @Transactional
    @Override
    public EmpleadorResponseDTO registrarEmpleador(EmpleadorRequestDTO requestDTO) {
        if (usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado.");
        }

        Empleador empleador = new Empleador();
        empleador.setNombre(requestDTO.getNombre());
        empleador.setEmail(requestDTO.getEmail());
        empleador.setPassword(requestDTO.getPassword());

        Empleador guardado = repository.save(empleador);
        return convertToResponseDto(guardado);
    }

    @Transactional
    @Override
    public EmpleadorResponseDTO actualizar(Long id, EmpleadorRequestDTO requestDTO) {
        Empleador existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleador no encontrado con ID: " + id));

        if (!existente.getEmail().equalsIgnoreCase(requestDTO.getEmail()) &&
                usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("El email ya está en uso por otro usuario.");
        }

        existente.setNombre(requestDTO.getNombre());
        existente.setEmail(requestDTO.getEmail());

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isBlank()) {
            existente.setPassword(requestDTO.getPassword());
        }

        Empleador actualizado = repository.save(existente);
        return convertToResponseDto(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Empleador no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    private EmpleadorResponseDTO convertToResponseDto(Empleador entity) {
        EmpleadorResponseDTO dto = new EmpleadorResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}