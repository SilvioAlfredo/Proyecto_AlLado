package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.NotificacionDTO;

import java.util.List;

/**
 * Avisos internos que el usuario consulta en la plataforma.
 *
 * <p>Guarda notificaciones, sin enviar email, WhatsApp ni push. Las preferencias del trabajador
 * no filtran estos avisos.</p>
 */
public interface NotificacionService {

    /**
     * @param usuarioId ID del titular; puede ser de otro usuario si quien ejecuta es ADMIN
     * @return notificaciones del usuario ordenadas por fecha de creación descendente
     */
    List<NotificacionDTO> listarPorUsuario(Long usuarioId);

    /**
     * @param usuarioId ID del titular; puede ser de otro usuario si quien ejecuta es ADMIN
     * @return cantidad de notificaciones con {@code leida=false}
     */
    long contarNoLeidas(Long usuarioId);

    /**
     * Marca una notificación como leída. Solo el destinatario o un ADMIN.
     *
     * @param id identificador de la notificación
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException si no existe
     */
    NotificacionDTO marcarLeida(Long id);

    /**
     * Marca como leídos todos los avisos pendientes del usuario.
     *
     * @param usuarioId ID del titular; puede ser de otro usuario si quien ejecuta es ADMIN
     */
    void marcarTodasLeidas(Long usuarioId);

    /**
     * Guarda un aviso interno para el destinatario indicado.
     *
     * <p>Tipo y referenciaId permiten relacionarlo con un evento y su entidad. Sin tipo usa
     * SISTEMA. No envía avisos por canales externos.</p>
     *
     * @param destinatarioId ID del usuario que recibirá la notificación
     * @param titulo obligatorio
     * @param mensaje obligatorio
     * @param tipo opcional; se usa el tipo de sistema si es nulo
     * @param referenciaId opcional; ID de la entidad relacionada
     * @return la notificación persistida
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si faltan título o mensaje
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe el destinatario
     */
    NotificacionDTO notificar(Long destinatarioId, String titulo, String mensaje, String tipo, Long referenciaId);
}
