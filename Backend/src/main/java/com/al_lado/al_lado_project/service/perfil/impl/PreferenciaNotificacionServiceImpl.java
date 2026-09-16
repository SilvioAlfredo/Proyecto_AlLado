package com.al_lado.al_lado_project.service.perfil.impl;

import com.al_lado.al_lado_project.dto.perfil.PreferenciaNotificacionDTO;
import com.al_lado.al_lado_project.entity.perfil.PreferenciaNotificacion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.perfil.PreferenciaNotificacionMapper;
import com.al_lado.al_lado_project.repository.perfil.PreferenciaNotificacionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.service.perfil.PreferenciaNotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreferenciaNotificacionServiceImpl implements PreferenciaNotificacionService {

    private final PreferenciaNotificacionRepository repository;
    private final TrabajadorRepository trabajadorRepository;
    private final PreferenciaNotificacionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PreferenciaNotificacionDTO obtenerPorTrabajador(Long trabajadorId) {
        PreferenciaNotificacion preferencia = repository.findByTrabajadorId(trabajadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Este trabajador todavía no tiene preferencias cargadas."));
        return mapper.toDTO(preferencia);
    }

    @Override
    @Transactional
    public PreferenciaNotificacionDTO guardar(Long trabajadorId, PreferenciaNotificacionDTO dto) {
        PreferenciaNotificacion preferencia = repository.findByTrabajadorId(trabajadorId)
                .orElseGet(() -> {
                    Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                            .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + trabajadorId));
                    PreferenciaNotificacion nueva = new PreferenciaNotificacion();
                    nueva.setTrabajador(trabajador);
                    return nueva;
                });

        preferencia.setNotificarEmail(dto.isNotificarEmail());
        preferencia.setNotificarPush(dto.isNotificarPush());
        preferencia.setAlertasNuevasOfertas(dto.isAlertasNuevasOfertas());

        return mapper.toDTO(repository.save(preferencia));
    }
}