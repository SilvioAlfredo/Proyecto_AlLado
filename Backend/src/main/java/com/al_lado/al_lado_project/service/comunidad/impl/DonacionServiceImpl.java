package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Donacion;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.DonacionMapper;
import com.al_lado.al_lado_project.repository.comunidad.DonacionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.service.comunidad.DonacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonacionServiceImpl implements DonacionService {

    private final DonacionRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final DonacionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<DonacionDTO> obtenerTodas() {
        return repository.findAllByOrderByFechaDonacionDesc().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DonacionDTO obtenerPorId(Long id) {
        Donacion donacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donación no encontrada con ID: " + id));
        return mapper.toDTO(donacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonacionDTO> obtenerPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DonacionDTO crear(DonacionDTO dto) {
        if (dto.getUsuario() == null || dto.getUsuario().getId() == null) {
            throw new BusinessRuleException("El usuario es obligatorio para registrar una donación.");
        }
        if (dto.getMonto() == null || dto.getMonto().signum() <= 0) {
            throw new BusinessRuleException("El monto de la donación debe ser mayor a cero.");
        }

        Long usuarioId = dto.getUsuario().getId();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Donacion donacion = mapper.toEntity(dto);
        donacion.setUsuario(usuario);

        return mapper.toDTO(repository.save(donacion));
    }
}