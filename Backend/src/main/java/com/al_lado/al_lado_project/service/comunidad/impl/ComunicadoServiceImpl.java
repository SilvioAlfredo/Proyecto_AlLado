package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.ComunicadoMapper;
import com.al_lado.al_lado_project.repository.comunidad.ComunicadoRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.service.comunidad.ComunicadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComunicadoServiceImpl implements ComunicadoService {

    private final ComunicadoRepository repository;
    private final AdministradorRepository administradorRepository;
    private final ComunicadoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ComunicadoDTO> obtenerTodos() {
        return repository.findAllByOrderByFechaEmisionDesc().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ComunicadoDTO obtenerPorId(Long id) {
        Comunicado comunicado = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comunicado no encontrado con ID: " + id));
        return mapper.toDTO(comunicado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComunicadoDTO> obtenerPorAlcance(String alcance) {
        return repository.findByAlcanceIgnoreCase(alcance).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComunicadoDTO crear(ComunicadoDTO dto) {
        if (dto.getEmisor() == null || dto.getEmisor().getId() == null) {
            throw new BusinessRuleException("El emisor (Administrador) es obligatorio.");
        }

        Long emisorId = dto.getEmisor().getId();
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