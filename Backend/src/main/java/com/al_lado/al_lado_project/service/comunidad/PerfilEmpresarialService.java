package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.PerfilEmpresarialDTO;

/**
 * Datos empresariales vinculados a un empleador.
 *
 * <p>El perfil tiene su propio identificador. No debe confundirse con el ID del empleador.</p>
 */
public interface PerfilEmpresarialService {

    /**
     * @param empleadorId ID del empleador dueño del perfil
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException si ese empleador no
     * tiene perfil cargado
     */
    PerfilEmpresarialDTO obtenerPorEmpleador(Long empleadorId);

    /**
     * Crea o actualiza el perfil del empleador.
     *
     * <p>Requiere al titular o ADMIN y PRO vigente. El CUIT no puede pertenecer a otro
     * empleador. Actualiza el perfil existente con los valores recibidos.</p>
     *
     * @param empleadorId ID del empleador dueño
     * @param dto datos del perfil; {@code cuit} opcional
     * @return el perfil guardado
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si no tiene PRO vigente
     * @throws com.al_lado.al_lado_project.exception.DuplicateResourceException
     *     si el CUIT pertenece a otro empleador
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe el empleador
     * @throws org.springframework.security.access.AccessDeniedException
     *     si actúa un tercero sin rol ADMIN
     */
    PerfilEmpresarialDTO guardar(Long empleadorId, PerfilEmpresarialDTO dto);

    /**
     * Marca el perfil como verificado y registra al administrador actual.
     *
     * <p>Requiere sesión ADMIN. Si se indica supervisorId, debe coincidir con esa sesión. Un
     * perfil ya verificado conserva su supervisor original. No hay fecha de verificación ni
     * acciones de rechazo o revocación.</p>
     *
     * @param id ID del <b>perfil</b>, no del empleador
     * @param supervisorId ID opcional del administrador supervisor
     * @return el perfil verificado
     * @throws org.springframework.security.access.AccessDeniedException
     *     si no hay sesión ADMIN o el supervisor no coincide con ella
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe el perfil o el administrador
     */
    PerfilEmpresarialDTO verificar(Long id, Long supervisorId);
}
