package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.ComunicadoMapper;
import com.al_lado.al_lado_project.repository.comunidad.ComunicadoRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.ComunicadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ComunicadoServiceImpl implements ComunicadoService {

    private final ComunicadoRepository repository;
    private final AdministradorRepository administradorRepository;
    private final ComunicadoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ComunicadoDTO> obtenerTodos(Pageable pageable) {
        return repository.findAllByOrderByFechaEmisionDesc(pageable).map(this::proyectar);
    }

    @Override
    @Transactional(readOnly = true)
    public ComunicadoDTO obtenerPorId(Long id) {
        Comunicado comunicado = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comunicado no encontrado con ID: " + id));
        return proyectar(comunicado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComunicadoDTO> obtenerPorAlcance(String alcance, Pageable pageable) {
        return repository.findByAlcanceIgnoreCase(alcance, pageable).map(this::proyectar);
    }

    /**
     * El comunicado se lee sin sesión: el email del emisor sólo lo ven la propia
     * administración emisora o un administrador (AL-SEC-006).
     */
    private ComunicadoDTO proyectar(Comunicado comunicado) {
        if (comunicado == null) return null;
        Long emisorId = comunicado.getEmisor() != null ? comunicado.getEmisor().getId() : null;
        return SecurityUtils.canViewPrivateDataOf(emisorId)
                ? mapper.toDTO(comunicado)
                : mapper.toDTOPublic(comunicado);
    }

    @Override
    @Transactional
    public ComunicadoDTO crear(ComunicadoDTO dto) {
        // AL-SEC-010: el emisor lo fija la sesión administrativa, no el cuerpo de la
        // petición. Si el cuerpo trae otro ID se ignora (no se puede firmar un
        // comunicado en nombre de otra cuenta); sólo cuando la sesión no trae la
        // entidad (p. ej. pruebas con principal genérico) se acepta el ID del cuerpo
        // y aun así sólo si es el propio usuario o un administrador.
        Long emisorResuelto;
        Usuario principal = SecurityUtils.currentUser();
        if (principal instanceof Administrador administrador && administrador.getId() != null) {
            emisorResuelto = administrador.getId();
        } else {
            if (dto.getEmisor() == null || dto.getEmisor().getId() == null) {
                throw new BusinessRuleException("El emisor (Administrador) es obligatorio.");
            }
            emisorResuelto = dto.getEmisor().getId();
            SecurityUtils.requireSelfOrAdmin(emisorResuelto);
        }

        final Long emisorId = emisorResuelto;
        Administrador emisor = administradorRepository.findById(emisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + emisorId));

        Comunicado comunicado = mapper.toEntity(dto);
        comunicado.setEmisor(emisor);

        return mapper.toDTO(repository.save(comunicado));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Comunicado no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}