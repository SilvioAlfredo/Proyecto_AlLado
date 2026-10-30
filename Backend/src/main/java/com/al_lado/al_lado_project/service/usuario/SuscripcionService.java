package com.al_lado.al_lado_project.service.usuario;

import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;

import java.util.List;

/**
 * Planes Básico y PRO de los usuarios.
 *
 * <p>El nivel puede establecerse al crear un plan o al verificar una transferencia de
 * suscripción. Tener nivel PRO por sí solo no confirma un pago.</p>
 */
public interface SuscripcionService {

    /** @return todas las suscripciones registradas, sin paginación. */
    List<SuscripcionDTO> obtenerTodas();


    SuscripcionDTO obtenerPorId(Long id);

    /**
     * @param usuarioId ID del titular; puede ser de otro usuario si quien ejecuta es ADMIN
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException si el usuario no
     * tiene suscripción cargada
     * @throws org.springframework.security.access.AccessDeniedException        si actúa un tercero
     * sin rol ADMIN
     */
    SuscripcionDTO obtenerPorUsuario(Long usuarioId);

    /**
     * Crea o reactiva el plan de un usuario.
     *
     * <p>Si ya existe, reutiliza el registro. Un usuario puede crear o reactivar solo el plan
     * Básico; PRO se concede únicamente al verificar una transferencia. Las fechas PRO las
     * establece esa operación de verificación.</p>
     *
     * @param dto debe traer {@code usuario.id}; el resto de campos se toman tal cual
     * @return la suscripción persistida (nueva o reactivada)
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si no se indica el usuario
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe el usuario
     * @throws org.springframework.security.access.AccessDeniedException
     *     si actúa un tercero sin rol ADMIN
     */
    SuscripcionDTO crear(SuscripcionDTO dto);

    /**
     * Desactiva el plan y conserva su registro y fechas.
     *
     * <p>Puede reactivarse mediante {@link #crear(SuscripcionDTO)}.</p>
     *
     * @param id identificador de la suscripción
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe o no tiene usuario asociado
     */
    SuscripcionDTO cancelar(Long id);

    /**
     * Borra el registro de la suscripción.
     *
     * <p>El permiso para eliminar se comprueba en el endpoint.</p>
     *
     * @param id identificador de la suscripción
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe
     */
    void eliminar(Long id);

    /**
     * Comprueba si el usuario puede crear otra oferta este mes.
     *
     * <p>El plan básico permite hasta tres ofertas que sigan activas y hayan sido creadas este
     * mes. Cerrar una libera cupo. Sin plan, con nivel nulo, Básico o plan PRO fuera de su
     * período de vigencia se aplica ese límite; solo PRO activo y vigente no tiene límite.</p>
     *
     * <p>Se utiliza al crear ofertas. El control de publicaciones simultáneas pertenece a ese
     * flujo, no a este método por separado.</p>
     *
     * @param usuarioId ID del empleador que publica
     * @return {@code true} si aún puede publicar este mes
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe el usuario
     */
    boolean puedePublicarOferta(Long usuarioId);

    /**
     * Comprueba si hay un plan PRO activo y vigente.
     *
     * <p>El período es inclusivo en ambas fechas; {@code fechaFin} nula significa sin
     * vencimiento. Una fecha de inicio futura aún no concede acceso.</p>
     *
     * @param usuarioId ID del usuario a consultar
     * @return {@code false} si no tiene suscripción o no cumple las condiciones
     */
    boolean esProVigente(Long usuarioId);
}
