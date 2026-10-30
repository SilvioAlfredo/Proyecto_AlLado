package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;
import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.PublicacionForoMapper;
import com.al_lado.al_lado_project.repository.comunidad.PublicacionForoRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.PublicacionForoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class PublicacionForoServiceImpl implements PublicacionForoService {

    private final PublicacionForoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PublicacionForoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PublicacionForoDTO> obtenerTodas(Pageable pageable) {
        return repository.findAllByOrderByFechaCreacionDesc(pageable).map(this::proyectar);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicacionForoDTO obtenerPorId(Long id) {
        PublicacionForo publicacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con ID: " + id));
        return proyectar(publicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PublicacionForoDTO> obtenerPorAutor(Long autorId, Pageable pageable) {
        return repository.findByAutorId(autorId, pageable).map(this::proyectar);
    }

    /**
     * GH-56 (HU-13): publicaciones de un sector concreto del foro.
     *
     * <p>El sector es una categoría pública del tema; la comparación no distingue
     * mayúsculas. Cada publicación se proyecta igual que en el resto de lecturas
     * (AL-SEC-006).</p>
     *
     * @param sector   nombre del sector, sin distinguir mayúsculas
     * @param pageable página y orden
     * @return publicaciones de ese sector, o vacío si no hay
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PublicacionForoDTO> obtenerPorSector(String sector, Pageable pageable) {
        if (sector == null || sector.isBlank()) {
            return repository.findAllByOrderByFechaCreacionDesc(pageable).map(this::proyectar);
        }
        return repository.findBySectorIgnoreCase(sector.trim(), pageable).map(this::proyectar);
    }

    /**
     * El foro es público: el email del autor no. Sólo el propio autor o un
     * administrador ven la publicación con la identidad completa (AL-SEC-006).
     */
    private PublicacionForoDTO proyectar(PublicacionForo publicacion) {
        if (publicacion == null) return null;
        Long autorId = publicacion.getAutor() != null ? publicacion.getAutor().getId() : null;
        return SecurityUtils.canViewPrivateDataOf(autorId)
                ? mapper.toDTO(publicacion)
                : mapper.toDTOPublic(publicacion);
    }

    @Override
    @Transactional
    public PublicacionForoDTO crear(PublicacionForoDTO dto) {
        if (dto.getAutor() == null || dto.getAutor().getId() == null) {
            throw new BusinessRuleException("El autor es obligatorio para crear una publicación.");
        }

        Long autorId = dto.getAutor().getId();
        SecurityUtils.requireSelfOrAdmin(autorId);
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + autorId));

        PublicacionForo publicacion = mapper.toEntity(dto);
        publicacion.setAutor(autor);

        return mapper.toDTO(repository.save(publicacion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        PublicacionForo publicacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con ID: " + id));
        Long autorId = publicacion.getAutor() != null ? publicacion.getAutor().getId() : null;
        SecurityUtils.requireSelfOrAdmin(autorId);
        repository.delete(publicacion);
    }
}