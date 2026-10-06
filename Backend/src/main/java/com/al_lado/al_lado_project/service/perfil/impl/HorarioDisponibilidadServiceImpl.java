package com.al_lado.al_lado_project.service.perfil.impl;

import com.al_lado.al_lado_project.dto.perfil.HorarioDisponibilidadDTO;
import com.al_lado.al_lado_project.entity.perfil.HorarioDisponibilidad;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.perfil.HorarioDisponibilidadMapper;
import com.al_lado.al_lado_project.repository.perfil.HorarioDisponibilidadRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.perfil.HorarioDisponibilidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioDisponibilidadServiceImpl implements HorarioDisponibilidadService {

    private final HorarioDisponibilidadRepository repository;
    private final TrabajadorRepository trabajadorRepository;
    private final HorarioDisponibilidadMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<HorarioDisponibilidadDTO> obtenerPorTrabajador(Long trabajadorId) {
        return repository.findByTrabajadorId(trabajadorId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HorarioDisponibilidadDTO agregar(Long trabajadorId, HorarioDisponibilidadDTO dto) {
        SecurityUtils.requireSelfOrAdmin(trabajadorId);
        if (dto.getHoraInicio() == null || dto.getHoraFin() == null) {
            throw new BusinessRuleException("La hora de inicio y de fin son obligatorias.");
        }
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
            throw new BusinessRuleException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        if (dto.getDiaSemana() == null || dto.getDiaSemana().isBlank()) {
            throw new BusinessRuleException("El día de la semana es obligatorio.");
        }

        String diaNormalizado = dto.getDiaSemana().trim();
        boolean solapado = repository.findByTrabajadorIdAndDiaSemanaIgnoreCase(trabajadorId, diaNormalizado).stream()
                .anyMatch(h -> h.getHoraInicio() != null && h.getHoraFin() != null
                        && dto.getHoraInicio().isBefore(h.getHoraFin())
                        && h.getHoraInicio().isBefore(dto.getHoraFin()));
        if (solapado) {
            throw new BusinessRuleException("El horario se solapa con otro ya registrado ese día.");
        }

        Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + trabajadorId));

        HorarioDisponibilidad horario = mapper.toEntity(dto);
        horario.setTrabajador(trabajador);

        return mapper.toDTO(repository.save(horario));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        HorarioDisponibilidad horario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con ID: " + id));
        Long propietarioId = horario.getTrabajador() != null ? horario.getTrabajador().getId() : null;
        SecurityUtils.requireSelfOrAdmin(propietarioId);
        repository.delete(horario);
    }
}