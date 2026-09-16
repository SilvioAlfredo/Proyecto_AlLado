package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;
import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.PublicacionForoMapper;
import com.al_lado.al_lado_project.repository.comunidad.PublicacionForoRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.service.comunidad.PublicacionForoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicacionForoServiceImpl implements PublicacionForoService {

    private final PublicacionForoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PublicacionForoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<PublicacionForoDTO> obtenerTodas() {
        return repository.findAllByOrderByFechaCreacionDesc().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PublicacionForoDTO obtenerPorId(Long id) {
        PublicacionForo publicacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con ID: " + id));
        return mapper.toDTO(publicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicacionForoDTO> obtenerPorAutor(Long autorId) {
        return repository.findByAutorId(autorId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PublicacionForoDTO crear(PublicacionForoDTO dto) {
        if (dto.getAutor() == null || dto.getAutor().getId() == null) {
            throw new BusinessRuleException("El autor es obligatorio para crear una publicación.");
        }

        Long autorId = dto.getAutor().getId();
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + autorId));

        PublicacionForo publicacion = mapper.toEntity(dto);
        publicacion.setAutor(autor);

        return mapper.toDTO(repository.save(publicacion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Publicación no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}