package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.PerfilEmpresarialDTO;
import com.al_lado.al_lado_project.entity.comunidad.PerfilEmpresarial;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.PerfilEmpresarialMapper;
import com.al_lado.al_lado_project.repository.comunidad.PerfilEmpresarialRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.service.comunidad.PerfilEmpresarialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PerfilEmpresarialServiceImpl implements PerfilEmpresarialService {

    private final PerfilEmpresarialRepository repository;
    private final EmpleadorRepository empleadorRepository;
    private final AdministradorRepository administradorRepository;
    private final PerfilEmpresarialMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PerfilEmpresarialDTO obtenerPorEmpleador(Long empleadorId) {
        PerfilEmpresarial perfil = repository.findByEmpleadorId(empleadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Este empleador no tiene perfil empresarial cargado."));
        return mapper.toDTO(perfil);
    }

    @Override
    @Transactional
    public PerfilEmpresarialDTO guardar(Long empleadorId, PerfilEmpresarialDTO dto) {
        Empleador empleador = empleadorRepository.findById(empleadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleador no encontrado con ID: " + empleadorId));

        if (dto.getCuit() != null && !dto.getCuit().isBlank()) {
            repository.findByCuit(dto.getCuit()).ifPresent(existente -> {
                if (!existente.getEmpleador().getId().equals(empleadorId)) {
                    throw new DuplicateResourceException("Ese CUIT ya está registrado por otro empleador.");
                }
            });
        }

        PerfilEmpresarial perfil = repository.findByEmpleadorId(empleadorId)
                .orElseGet(() -> mapper.toEntity(dto));

        perfil.setNombreEmpresa(dto.getNombreEmpresa());
        perfil.setCuit(dto.getCuit());
        perfil.setDescripcion(dto.getDescripcion());
        perfil.setSitioWeb(dto.getSitioWeb());
        perfil.setUrlLogo(dto.getUrlLogo());
        perfil.setEmpleador(empleador);

        return mapper.toDTO(repository.save(perfil));
    }

    @Override
    @Transactional
    public PerfilEmpresarialDTO verificar(Long id, Long supervisorId) {
        PerfilEmpresarial perfil = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil empresarial no encontrado con ID: " + id));

        Administrador supervisor = administradorRepository.findById(supervisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + supervisorId));

        perfil.setVerificado(true);
        perfil.setSupervisadoPor(supervisor);

        return mapper.toDTO(repository.save(perfil));
    }
}