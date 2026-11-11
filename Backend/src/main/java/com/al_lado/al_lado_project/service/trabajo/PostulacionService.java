package com.al_lado.al_lado_project.service.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.dto.usuario.EstadisticasTrabajadorDTO;

import java.util.List;

/**
 * Postulaciones de trabajadores y cambios de estado.
 *
 * <p>Los cambios conservan el registro y generan notificaciones internas. Los controllers
 * comprueban los roles; el servicio comprueba quién es el titular.</p>
 *
 * <ul><li>PENDIENTE → SELECCIONADA → COMPLETADA.</li><li>PENDIENTE o SELECCIONADA → RECHAZADA o
 * RETIRADA.</li></ul>
 */
public interface PostulacionService {

    /** @return todas las postulaciones del sistema, sin filtros ni paginación. */
    List<PostulacionDTO> obtenerTodas();


    PostulacionDTO obtenerPorId(Long id);

    /**
     * Crea una postulación en estado PENDIENTE y avisa al empleador.
     *
     * <p>La oferta debe estar activa y el trabajador no debe haberse postulado antes. Ignora el
     * estado recibido. El servicio admite al trabajador titular o ADMIN; el endpoint de creación
     * exige TRABAJADOR.</p>
     *
     * @param postulacionDTO debe traer {@code autor.id} (trabajador) y {@code
     *     oportunidadLaboral.id}
     * @return la postulación recién creada con su ID asignado
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si faltan datos o la oportunidad está cerrada
     * @throws com.al_lado.al_lado_project.exception.DuplicateResourceException
     *     si el trabajador ya postuló a esa oferta
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existen el trabajador o la oportunidad
     * @throws org.springframework.security.access.AccessDeniedException
     *     si actúa un tercero sin rol ADMIN
     */
    PostulacionDTO crearPostulacion(PostulacionDTO postulacionDTO);

    /**
     * Selecciona una postulación PENDIENTE y avisa al trabajador.
     *
     * <p>Solo puede hacerlo el empleador dueño de la oferta o ADMIN.</p>
     *
     * @param id identificador de la postulación
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si no está {@code PENDIENTE} o la oferta asociada es inválida
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe la postulación
     */
    PostulacionDTO seleccionarPostulacion(Long id);

    /**
     * Marca como COMPLETADA una postulación SELECCIONADA y avisa al trabajador.
     *
     * <p>Solo puede hacerlo el empleador dueño de la oferta o ADMIN.</p>
     *
     * @param id identificador de la postulación
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si no está {@code SELECCIONADA}
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe
     */
    PostulacionDTO completarPostulacion(Long id);

    /**
     * Rechaza una postulación PENDIENTE o SELECCIONADA y avisa al trabajador.
     *
     * <p>Solo puede hacerlo el empleador dueño de la oferta o ADMIN.</p>
     *
     * @param id identificador de la postulación
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si ya está en un estado final
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe
     */
    PostulacionDTO rechazarPostulacion(Long id);

    /**
     * Retira una postulación PENDIENTE o SELECCIONADA y avisa al empleador.
     *
     * <p>El servicio admite al trabajador titular o ADMIN; el endpoint de retiro exige
     * TRABAJADOR.</p>
     *
     * @param id identificador de la postulación
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si no está en estado abierto o el autor no es trabajador
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe
     */
    PostulacionDTO retirarPostulacion(Long id);

    /**
     * Obtiene los conteos y datos del perfil de un trabajador.
     *
     * <p>Los ingresos y ofertas publicadas devuelven cero, el nivel de recomendación devuelve 4
     * y el último ingreso es nulo: son datos aún no implementados. {@code perfilCompleto} solo
     * indica que hay fotografía.</p>
     *
     * <p>{@code suscripcionActiva} lee el indicador activa; no comprueba nivel PRO ni
     * vencimiento. Ver {@link
     * com.al_lado.al_lado_project.service.usuario.SuscripcionService#esProVigente(Long)}.</p>
     *
     * @param trabajadorId ID del titular; puede ser de otro usuario si quien ejecuta es ADMIN
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe el trabajador
     */
    EstadisticasTrabajadorDTO obtenerEstadisticasTrabajador(Long trabajadorId);

    /**
     * Devuelve las mismas estadísticas buscando al trabajador por email.
     *
     * <p>Aplica las mismas reglas de acceso y conserva los valores aún no implementados.</p>
     *
     * @param email email del trabajador
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe un trabajador con ese email
     */
    EstadisticasTrabajadorDTO obtenerEstadisticasTrabajadorPorEmail(String email);
}
