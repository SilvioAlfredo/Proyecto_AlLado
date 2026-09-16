package com.al_lado.al_lado_project.service.perfil.impl;

import com.al_lado.al_lado_project.dto.perfil.PortafolioItemDTO;
import com.al_lado.al_lado_project.entity.perfil.PortafolioItem;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.perfil.PortafolioItemMapper;
import com.al_lado.al_lado_project.repository.perfil.PortafolioItemRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.service.perfil.PortafolioItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortafolioItemServiceImpl implements PortafolioItemService {

    private final PortafolioItemRepository repository;
    private final TrabajadorRepository trabajadorRepository;
    private final PortafolioItemMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<PortafolioItemDTO> obtenerPorTrabajador(Long trabajadorId) {
        return repository.findByTrabajadorId(trabajadorId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PortafolioItemDTO agregar(Long trabajadorId, PortafolioItemDTO dto) {
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + trabajadorId));

        PortafolioItem item = mapper.toEntity(dto);
        item.setTrabajador(trabajador);

        return mapper.toDTO(repository.save(item));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Ítem de portafolio no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}