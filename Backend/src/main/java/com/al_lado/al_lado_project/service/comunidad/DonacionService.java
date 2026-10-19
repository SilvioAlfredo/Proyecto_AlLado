package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Registro y verificación de aportes declarados.
 *
 * <p>El registro requiere al titular o ADMIN; el endpoint de verificación exige ADMIN. Estos
 * métodos no mueven dinero ni consultan bancos.</p>
 */
public interface DonacionService {

    /**
     * @param pageable página y orden (orden por fecha de donación descendente)
     * @return página de donaciones; no aplica filtros de estado
     */
    Page<DonacionDTO> obtenerTodas(Pageable pageable);

    /**
     * @param id identificador de la donación
     * @return la donación; solo el dueño o un ADMIN pueden leerla
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException si no existe
     */
    DonacionDTO obtenerPorId(Long id);

    /**
     * @param usuarioId ID del titular; puede ser de otro usuario si quien ejecuta es ADMIN
     */
    Page<DonacionDTO> obtenerPorUsuario(Long usuarioId, Pageable pageable);

    /**
     * Registra un aporte con monto positivo y estado PENDIENTE.
     *
     * <p>Ignora el estado recibido. Conserva los datos bancarios de los mecanismos TRANSFERENCIA
     * y SUSCRIPCION para su posterior revisión.</p>
     *
     * @param dto debe traer {@code usuario.id} y {@code monto} &gt; 0
     * @return la donación persistida en estado {@code PENDIENTE}
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si faltan datos o el monto no es positivo
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe el usuario
     * @throws org.springframework.security.access.AccessDeniedException
     *     si actúa un tercero sin rol ADMIN
     */
    DonacionDTO crear(DonacionDTO dto);

    /**
     * Verifica un aporte pendiente usando el asunto y monto observados en el banco.
     *
     * <p>El asunto debe coincidir exactamente. El monto admite diferencias de decimales
     * escritos: 100.0 equivale a 100.00. Valores nulos o distintos se rechazan sin modificar el
     * registro.</p>
     *
     * <p>Deja el aporte VERIFICADO. Si corresponde a una suscripción, crea o reactiva PRO por un
     * año desde hoy, sin sumar ese año al vencimiento anterior. Una transferencia ya verificada
     * no puede verificarse nuevamente.</p>
     *
     * <p>Dos verificaciones del mismo aporte se procesan por turno. Esa protección no abarca
     * aportes distintos, aunque pertenezcan al mismo usuario.</p>
     *
     * @param id identificador de la donación
     * @param asuntoEsperado asunto que el administrador verifica en el registro bancario
     * @param montoEsperado monto que el administrador verifica en el registro bancario
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si no está pendiente, no coinciden asunto/monto o falta usuario en una
     *     suscripción
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe la donación o el usuario
     */
    DonacionDTO verificarTransferencia(Long id, String asuntoEsperado, BigDecimal montoEsperado);
}
