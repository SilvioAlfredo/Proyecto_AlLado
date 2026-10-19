package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.NotificacionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Notificacion;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.NotificacionMapper;
import com.al_lado.al_lado_project.repository.comunidad.NotificacionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionDTO> listarPorUsuario(Long usuarioId) {
        SecurityUtils.requireSelfOrAdmin(usuarioId);
        return repository.findByDestinatarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long contarNoLeidas(Long usuarioId) {
        SecurityUtils.requireSelfOrAdmin(usuarioId);
        return repository.countByDestinatarioIdAndLeidaFalse(usuarioId);
    }

    @Override
    @Transactional
    public NotificacionDTO marcarLeida(Long id) {
        Notificacion notificacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));
        Long propietarioId = notificacion.getDestinatario() != null ? notificacion.getDestinatario().getId() : null;
        SecurityUtils.requireSelfOrAdmin(propietarioId);
        notificacion.setLeida(true);
        return mapper.toDTO(repository.save(notificacion));
    }

    /**
     * Actualiza entidades gestionadas dentro de la transacción.
     * Los cambios se guardan al finalizarla, sin llamar a save por cada aviso.
     */
    @Override
    @Transactional
    public void marcarTodasLeidas(Long usuarioId) {
        SecurityUtils.requireSelfOrAdmin(usuarioId);
        repository.findByDestinatarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .filter(n -> !n.isLeida())
                .forEach(n -> n.setLeida(true));
    }

    @Override
    @Transactional
    public NotificacionDTO notificar(Long destinatarioId, String titulo, String mensaje, String tipo, Long referenciaId) {
        if (titulo == null || titulo.isBlank()) {
            throw new BusinessRuleException("El título de la notificación es obligatorio.");
        }
        if (mensaje == null || mensaje.isBlank()) {
            throw new BusinessRuleException("El mensaje de la notificación es obligatorio.");
        }
        Usuario destinatario = usuarioRepository.findById(destinatarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + destinatarioId));

        Notificacion notificacion = new Notificacion(
                destinatario, titulo, mensaje,
                tipo != null ? tipo : Notificacion.TIPO_SISTEMA, referenciaId);

        return mapper.toDTO(repository.save(notificacion));
    }
}
