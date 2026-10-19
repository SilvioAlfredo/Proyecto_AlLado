package com.al_lado.al_lado_project.service.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.FiltrosOportunidadesDTO;
import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Creación, edición y búsqueda de ofertas de trabajo.
 *
 * <p>Las modificaciones requieren al empleador titular o ADMIN. Las búsquedas devuelven ofertas
 * activas; {@link #obtenerTodas()} y {@link #obtenerPorId(Long)} también pueden devolver
 * cerradas.</p>
 */
public interface OportunidadLaboralService {

    /**
     * @return todas las ofertas, <b>incluidas las cerradas</b>; sin paginación.
     */
    List<OportunidadLaboralDTO> obtenerTodas();

    /**
     * Página de ofertas activas según el texto libre.
     *
     * @param filtro    texto opcional; sin filtro devolvería todas las activas
     * @param pageable  página y orden (la página la resuelve Spring Data)
     */
    Page<OportunidadLaboralDTO> listarOportunidades(String filtro, Pageable pageable);

    /**
     * Igual que {@link #listarOportunidades(String, Pageable)} añadiendo modalidad
     * y etiquetas como criterios acumulativos.
     *
     * @param modalidad opcional: {@code REMOTO}, {@code PRESENCIAL} o {@code HIBRIDO}
     * @param etiquetas opcional; basta con <b>una</b> coincidencia (no todas)
     */
    Page<OportunidadLaboralDTO> listarOportunidades(String filtro, String modalidad, List<String> etiquetas, Pageable pageable);

    /**
     * @param id identificador de la oferta
     * @return la oferta <b>sin importar si está activa o cerrada</b>
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException si no existe
     */
    OportunidadLaboralDTO obtenerPorId(Long id);

    /**
     * Crea o actualiza una oferta del empleador indicado.
     *
     * <p>El alta queda activa y comprueba el cupo básico. La edición toma {@code activa} del
     * DTO: omitirlo equivale a false y no conserva el valor anterior. Cerrar o reabrir no vuelve
     * a comprobar el cupo.</p>
     *
     * <p>Destacar requiere PRO vigente. Solo ADMIN puede cambiar el empleador. Las publicaciones
     * del mismo empleador se procesan por turno para proteger el control del cupo.</p>
     *
     * @param oportunidadDTO sin {@code id} para crear; con {@code id} para editar. Las etiquetas
     *     se resuelven por ID o se crean por nombre.
     * @return la oferta persistida
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si faltan datos, se excede el cupo, se destaca sin PRO o se intenta transferir
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existen el empleador, la oferta o una etiqueta referenciada
     * @throws org.springframework.security.access.AccessDeniedException
     *     si actúa un tercero sin rol ADMIN
     */
    OportunidadLaboralDTO guardar(OportunidadLaboralDTO oportunidadDTO);

    /**
     * Desactiva una oferta sin eliminarla ni modificar sus postulaciones.
     *
     * <p>Solo el titular o ADMIN. Deja de contar para el cupo básico si fue creada este mes.</p>
     *
     * @param id identificador de la oferta
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe
     */
    OportunidadLaboralDTO cerrarOportunidad(Long id);

    /**
     * Elimina una oferta del titular o de una cuenta administrada por ADMIN.
     *
     * <p>Las relaciones existentes pueden impedirlo. El error recibido depende del momento en
     * que la base de datos detecte esa restricción.</p>
     *
     * @param id identificador de la oferta
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si hay postulaciones asociadas y la violación se detecta en la transacción
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe
     */
    void eliminar(Long id);

    /**
     * @param nombreEtiqueta nombre de la especialidad/etiqueta
     * @return trabajadores con esa especialidad; vacío si no hay coincidencias
     */
    List<Trabajador> obtenerTrabajadoresPorEtiqueta(String nombreEtiqueta);

    /**
     * Busca ofertas activas que cumplan los filtros indicados.
     *
     * <p>Combina los filtros entre sí; el texto puede coincidir en cualquiera de sus campos.
     * Basta con una etiqueta coincidente. Cada oferta aparece una sola vez.</p>
     *
     * @param filtro texto libre contra título, contenido, ubicación y nombre de etiqueta
     * @param modalidad modalidad exacta sin distinguir mayúsculas
     * @param etiquetas la oferta debe tener <b>al menos una</b> de estas etiquetas (se ignoran
     *     nulos y vacíos)
     * @return solo ofertas activas; lista vacía si nada coincide
     */
    List<OportunidadLaboralDTO> buscarPorFiltros(String filtro, String modalidad, List<String> etiquetas);

    /**
     * Busca ofertas activas por texto, modalidad, etiquetas y opción de destacadas.
     *
     * <p>Devuelve la lista completa. Para obtener páginas, utilizar {@code listarOportunidades}
     * con {@link Pageable}.</p>
     *
     * @param filtros puede ser {@code null}; los campos individuales también son opcionales
     * @return solo ofertas activas que cumplan todos los criterios indicados
     */
    List<OportunidadLaboralDTO> listarConFiltros(FiltrosOportunidadesDTO filtros);

    /**
     * Busca ofertas cuyas etiquetas coincidan con las especialidades del trabajador.
     *
     * <p>Requiere PRO vigente. Prioriza destacadas y acepta al menos una coincidencia. Sin
     * especialidades devuelve una lista vacía.</p>
     *
     * @param trabajadorId ID del titular; puede ser de otro usuario si quien ejecuta es ADMIN
     * @return ofertas activas compatibles, destacadas primero
     * @throws com.al_lado.al_lado_project.exception.BusinessRuleException
     *     si no tiene PRO vigente
     * @throws com.al_lado.al_lado_project.exception.ResourceNotFoundException
     *     si no existe el trabajador
     * @throws org.springframework.security.access.AccessDeniedException
     *     si actúa un tercero sin rol ADMIN
     */
    List<OportunidadLaboralDTO> propuestasProParaTrabajador(Long trabajadorId);
}
