package com.al_lado.al_lado_project.service.impl;

import com.al_lado.al_lado_project.dto.TrabajadorRequestDTO;
import com.al_lado.al_lado_project.dto.TrabajadorResponseDTO;
import com.al_lado.al_lado_project.entity.Trabajador;
import com.al_lado.al_lado_project.repository.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.UsuarioRepository;
import com.al_lado.al_lado_project.service.TrabajadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements TrabajadorService {

    private final TrabajadorRepository repository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorResponseDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TrabajadorResponseDTO obtenerPorId(Long id) {
        Trabajador trabajador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado con ID: " + id));
        return convertToResponseDto(trabajador);
    }

    @Override
    public Trabajador guardar(Trabajador trabajador) {
        return null;
    }

    @Override
    @Transactional
    public TrabajadorResponseDTO guardar(TrabajadorRequestDTO requestDTO) {
        if (usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado.");
        }

        Trabajador trabajador = new Trabajador();
        trabajador.setNombre(requestDTO.getNombre());
        trabajador.setEmail(requestDTO.getEmail());
        trabajador.setPassword(requestDTO.getPassword());

        Trabajador guardado = repository.save(trabajador);
        return convertToResponseDto(guardado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Trabajador no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    private TrabajadorResponseDTO convertToResponseDto(Trabajador entity) {
        TrabajadorResponseDTO dto = new TrabajadorResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}