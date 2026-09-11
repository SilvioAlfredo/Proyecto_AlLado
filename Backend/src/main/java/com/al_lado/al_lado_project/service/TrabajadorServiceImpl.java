package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.TrabajadorDTO;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.mappers.TrabajadorMapper;
import com.al_lado.al_lado_project.repository.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.UsuarioRepository;
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
    private final TrabajadorMapper mapper;

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
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado con ID: " + id));
        return mapper.toDTO(trabajador);
    }

    @Override
    @Transactional
    public TrabajadorDTO guardar(TrabajadorDTO dto) {
        Trabajador trabajador;

        if (dto.getId() == null) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("El email ya está registrado.");
            }
            trabajador = mapper.toEntity(dto);
        } else {
            trabajador = repository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

            if (!trabajador.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("El email ya está registrado por otro usuario.");
            }
        }

        trabajador.setNombre(dto.getNombre());
        trabajador.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            trabajador.setPassword(dto.getPassword());
        }

        return mapper.toDTO(repository.save(trabajador));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Trabajador no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}