package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.usuario.SuscripcionMapper;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuscripcionServiceImpl implements SuscripcionService {

    private final SuscripcionRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final SuscripcionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SuscripcionDTO obtenerPorId(Long id) {
        Suscripcion suscripcion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripción no encontrada con ID: " + id));
        return mapper.toDTO(suscripcion);
    }

    @Override
    @Transactional(readOnly = true)
    public SuscripcionDTO obtenerPorUsuario(Long usuarioId) {
        Suscripcion suscripcion = repository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no tiene una suscripción cargada."));
        return mapper.toDTO(suscripcion);
    }

    @Override
    @Transactional
    public SuscripcionDTO crear(SuscripcionDTO dto) {
        if (dto.getUsuario() == null || dto.getUsuario().getId() == null) {
            throw new BusinessRuleException("El usuario es obligatorio para crear una suscripción.");
        }

        Long usuarioId = dto.getUsuario().getId();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        if (repository.existsByUsuarioId(usuarioId)) {
            throw new DuplicateResourceException("Este usuario ya tiene una suscripción cargada.");
        }

        Suscripcion suscripcion = mapper.toEntity(dto);
        suscripcion.setUsuario(usuario);
        suscripcion.setActiva(true);

        return mapper.toDTO(repository.save(suscripcion));
    }

    @Override
    @Transactional
    public SuscripcionDTO cancelar(Long id) {
        Suscripcion suscripcion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripción no encontrada con ID: " + id));

        suscripcion.setActiva(false);
        return mapper.toDTO(repository.save(suscripcion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Suscripción no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}