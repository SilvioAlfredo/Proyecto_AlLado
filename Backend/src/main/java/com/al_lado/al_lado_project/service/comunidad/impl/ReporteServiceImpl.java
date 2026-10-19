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
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;
    private final ReporteMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ReporteDTO> obtenerTodos(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDTO);
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
    public Page<ReporteDTO> obtenerPorEstado(String estado, Pageable pageable) {
        return repository.findByEstadoIgnoreCase(estado, pageable).map(mapper::toDTO);
    }

    @Override
    @Transactional
    public ReporteDTO crear(ReporteDTO dto) {
        if (dto.getReportante() == null || dto.getReportante().getId() == null) {
            throw new BusinessRuleException("El reportante es obligatorio.");
        }

        Long reportanteId = dto.getReportante().getId();
        SecurityUtils.requireSelfOrAdmin(reportanteId);
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

        if ("RESUELTO".equalsIgnoreCase(reporte.getEstado())
                || "DESCARTADO".equalsIgnoreCase(reporte.getEstado())) {
            throw new BusinessRuleException("El reporte ya fue moderado y no puede re-resolverse.");
        }

        String estadoFinal = nuevoEstado != null ? nuevoEstado : "RESUELTO";
        if (!"EN_REVISION".equalsIgnoreCase(estadoFinal)
                && !"RESUELTO".equalsIgnoreCase(estadoFinal)
                && !"DESCARTADO".equalsIgnoreCase(estadoFinal)) {
            throw new BusinessRuleException("Estado inválido. Valores permitidos: EN_REVISION, RESUELTO, DESCARTADO.");
        }

        // Sincronía con el frontend: no envía moderadorId porque no conoce
        // su id numérico; se usa el admin autenticado (el endpoint ya exige ROLE_ADMIN).
        Long moderadorEfectivo = moderadorId != null ? moderadorId
                : com.al_lado.al_lado_project.security.SecurityUtils.currentUserId();
        if (moderadorEfectivo == null) {
            throw new BusinessRuleException("No se pudo determinar el moderador autenticado.");
        }
        Administrador moderador = administradorRepository.findById(moderadorEfectivo)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + moderadorEfectivo));

        reporte.setModerador(moderador);
        reporte.setEstado(estadoFinal.toUpperCase());

        return mapper.toDTO(repository.save(reporte));
    }
}