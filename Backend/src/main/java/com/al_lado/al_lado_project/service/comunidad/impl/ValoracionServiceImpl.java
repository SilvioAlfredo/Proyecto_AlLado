package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.ValoracionMapper;
import com.al_lado.al_lado_project.repository.comunidad.ValoracionRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.ValoracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ValoracionServiceImpl implements ValoracionService {

    private final ValoracionRepository repository;
    private final EmpleadorRepository empleadorRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final ValoracionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ValoracionDTO> obtenerPorTrabajador(Long trabajadorId, Pageable pageable) {
        return repository.findByTrabajadorId(trabajadorId, pageable).map(this::proyectar);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ValoracionDTO> obtenerPorAutor(Long autorId, Pageable pageable) {
        return repository.findByAutorId(autorId, pageable).map(this::proyectar);
    }

    /**
     * Las valoraciones son públicas (puntuación y comentario), los emails de quien
     * valora y de quien es valorado no: sólo el propio usuario o un administrador
     * reciben la identidad completa (AL-SEC-006).
     */
    private ValoracionDTO proyectar(Valoracion valoracion) {
        if (valoracion == null) return null;
        Long autorId = valoracion.getAutor() != null ? valoracion.getAutor().getId() : null;
        Long trabajadorId = valoracion.getTrabajador() != null ? valoracion.getTrabajador().getId() : null;
        boolean completo = SecurityUtils.canViewPrivateDataOf(autorId)
                || SecurityUtils.canViewPrivateDataOf(trabajadorId);
        return completo ? mapper.toDTO(valoracion) : mapper.toDTOPublic(valoracion);
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerPromedioPorTrabajador(Long trabajadorId) {
        Double promedio = repository.calcularPromedioPuntuacionPorTrabajadorId(trabajadorId);
        return promedio != null ? promedio : 0.0;
    }

    @Override
    @Transactional
    public ValoracionDTO crear(ValoracionDTO dto) {
        if (dto.getAutor() == null || dto.getAutor().getId() == null) {
            throw new BusinessRuleException("El autor (Empleador) es obligatorio.");
        }
        if (dto.getTrabajador() == null || dto.getTrabajador().getId() == null) {
            throw new BusinessRuleException("El trabajador valorado es obligatorio.");
        }
        if (dto.getPuntuacion() == null || dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5) {
            throw new BusinessRuleException("La puntuación debe estar entre 1 y 5.");
        }

        Long autorId = dto.getAutor().getId();
        Long trabajadorId = dto.getTrabajador().getId();
        SecurityUtils.requireSelfOrAdmin(autorId);

        if (repository.existsByAutorIdAndTrabajadorId(autorId, trabajadorId)) {
            throw new DuplicateResourceException("Ya existe una valoración de este empleador a este trabajador.");
        }

        Empleador autor = empleadorRepository.findById(autorId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleador no encontrado con ID: " + autorId));
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + trabajadorId));

        Valoracion valoracion = mapper.toEntity(dto);
        valoracion.setAutor(autor);
        valoracion.setTrabajador(trabajador);

        return mapper.toDTO(repository.save(valoracion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Valoracion valoracion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoración no encontrada con ID: " + id));
        Long autorId = valoracion.getAutor() != null ? valoracion.getAutor().getId() : null;
        SecurityUtils.requireSelfOrAdmin(autorId);
        repository.delete(valoracion);
    }
}