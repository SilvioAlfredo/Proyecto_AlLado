package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.ReporteDTO;
import com.al_lado.al_lado_project.entity.comunidad.Reporte;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.ReporteMapper;
import com.al_lado.al_lado_project.repository.comunidad.ReporteRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.service.comunidad.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;
    private final ReporteMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteDTO obtenerPorId(Long id) {
        Reporte reporte = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));
        return mapper.toDTO(reporte);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDTO> obtenerPorEstado(String estado) {
        return repository.findByEstadoIgnoreCase(estado).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReporteDTO crear(ReporteDTO dto) {
        if (dto.getReportante() == null || dto.getReportante().getId() == null) {
            throw new BusinessRuleException("El reportante es obligatorio.");
        }

        Long reportanteId = dto.getReportante().getId();
        Usuario reportante = usuarioRepository.findById(reportanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario reportante no encontrado con ID: " + reportanteId));

        Reporte reporte = mapper.toEntity(dto);
        reporte.setReportante(reportante);
        reporte.setEstado("PENDIENTE");

        if (dto.getReportado() != null && dto.getReportado().getId() != null) {
            Usuario reportado = usuarioRepository.findById(dto.getReportado().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario reportado no encontrado con ID: " + dto.getReportado().getId()));
            reporte.setReportado(reportado);
        }

        return mapper.toDTO(repository.save(reporte));
    }

    @Override
    @Transactional
    public ReporteDTO resolver(Long id, Long moderadorId, String nuevoEstado) {
        Reporte reporte = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));

        Administrador moderador = administradorRepository.findById(moderadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + moderadorId));

        reporte.setModerador(moderador);
        reporte.setEstado(nuevoEstado != null ? nuevoEstado : "RESUELTO");

        return mapper.toDTO(repository.save(reporte));
    }
}