package com.al_lado.al_lado_project.repository.trabajo;

import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de oportunidades laborales.
 *
 * <p>{@link #findTendenciasPorEtiqueta()} alimenta el Dashboard y
 * {@link #countByAutorIdAndActivaAndFechaCreacionBetween(Long, boolean, LocalDateTime,
 * LocalDateTime)}
 * cuenta ofertas para el cupo básico. Las búsquedas combinadas del servicio usan
 * {@code Specification}; las consultas de esta interfaz aplican los criterios
 * específicos documentados en cada método.</p>
 */
@Repository
public interface OportunidadLaboralRepository
        extends JpaRepository<OportunidadLaboral, Long>,
        JpaSpecificationExecutor<OportunidadLaboral> {

    //  Usadas por los services


    @EntityGraph(attributePaths = {"etiquetas", "autor"})
    List<OportunidadLaboral> findByActivaTrue();


    List<OportunidadLaboral> findByAutorId(Long autorId);


    List<OportunidadLaboral> findByAutorIdAndActiva(Long autorId, boolean activa);

    /**
     * RF-09: tendencias por etiqueta para el dashboard administrativo.
     *
     * <p>Hace {@code JOIN} (no {@code LEFT JOIN}) entre oportunidades y etiquetas y
     * agrupa por nombre de etiqueta: <b>una oportunidad con varias etiquetas cuenta
     * una vez por cada etiqueta</b> y las que no tienen etiquetas no aparecen, por lo
     * que las categorías no son excluyentes y los porcentajes calculados a partir de
     * esta lista pueden sumar más de 100. Ordena por cantidad descendente; el
     * dashboard toma solo las primeras cinco.</p>
     *
     * @return pares {@code [nombreDeEtiqueta, cantidadDeOportunidades]}
     */
    @Query("SELECT e.nombre, COUNT(o) " +
            "FROM OportunidadLaboral o JOIN o.etiquetas e " +
            "GROUP BY e.nombre " +
            "ORDER BY COUNT(o) DESC")
    List<Object[]> findTendenciasPorEtiqueta();

    /**
     * RF-04 / HU-01: cupo de 3 ofertas mensuales del plan Básico.
     *
     * <p>Cuenta las ofertas del autor que siguen <b>activas</b> y cuya
     * {@code fechaCreacion} cae en el rango recibido. El rango lo construye
     * {@code SuscripcionServiceImpl} como mes calendario completo cerrado (día 1 a
     * las 00:00:00 → último día a las 23:59:59); {@code fechaCreacion} es
     * {@code LocalDateTime}, no {@code LocalDate}.</p>
     *
     * <p>Como exige {@code activa = true}, cerrar una oferta hace que deje de
     * contar hacia el cupo.</p>
     *
     * @param start inicio del rango inclusivo
     * @param end   fin del rango inclusivo
     */
    long countByAutorIdAndActivaAndFechaCreacionBetween(Long autorId, boolean activa,
                                                        LocalDateTime start, LocalDateTime end);

    // ------------------------------------------------------------------
    // Consultas sin llamadores en el código actual (servicios ni pruebas).
    // Se conservan documentadas porque su semántica difiere de la del negocio.
    // ------------------------------------------------------------------

    // Paginación


    Page<OportunidadLaboral> findAllByActivaTrue(Pageable pageable);


    Page<OportunidadLaboral> findByAutorIdAndActiva(Long autorId, boolean activa, Pageable pageable);

    // Antes era query derivada sobre la propiedad "etiqueta", que no existe
    // (el campo se llama "etiquetas"). Ahora es un @Query explícito.
    /**
     * Ofertas <b>activas</b> con <b>al menos una</b> de las etiquetas
     * indicadas (operador {@code IN}, no «todas»), con {@code DISTINCT} para no
     * repetir la oferta si coincide por varias etiquetas; el {@code countQuery}
     * también cuenta distintos, así que la paginación es coherente.
     *
     * <p>Difiere del filtro del negocio: la búsqueda por {@code Specification} acepta
     * texto libre y modalidad además de etiquetas.</p>
     */
    @Query(value = "SELECT DISTINCT o FROM OportunidadLaboral o " +
            "JOIN o.etiquetas e " +
            "WHERE e.nombre IN :etiquetas AND o.activa = true",
            countQuery = "SELECT COUNT(DISTINCT o) FROM OportunidadLaboral o " +
                    "JOIN o.etiquetas e " +
                    "WHERE e.nombre IN :etiquetas AND o.activa = true")
    Page<OportunidadLaboral> findByEtiquetaIn(@Param("etiquetas") List<String> etiquetas,
                                              Pageable pageable);

    /**
     * Ofertas activas cuyo <b>título o contenido</b> contengan el
     * término: a diferencia de la búsqueda del negocio <b>no revisa ubicación ni
     * nombres de etiqueta</b>.
     */
    @EntityGraph(attributePaths = {"etiquetas", "autor"})
    @Query("SELECT o FROM OportunidadLaboral o WHERE " +
            "(LOWER(o.titulo) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(o.contenido) LIKE LOWER(CONCAT('%', :filtro, '%'))) " +
            "AND o.activa = true")
    List<OportunidadLaboral> buscarActivasPorTermino(@Param("filtro") String filtro);

    // Búsqueda por etiqueta

    // Antes: query derivada sobre "etiquetaNombre", propiedad inexistente.
    /**
     * Ofertas activas con una etiqueta cuyo nombre sea exacto,
     * comparado sin distinguir mayúsculas.
     */
    @Query("SELECT DISTINCT o FROM OportunidadLaboral o " +
            "JOIN o.etiquetas e " +
            "WHERE LOWER(e.nombre) = LOWER(:nombreEtiqueta) " +
            "AND o.activa = true")
    List<OportunidadLaboral> findByEtiquetaNombre(@Param("nombreEtiqueta") String nombreEtiqueta);


    @Query("SELECT DISTINCT o FROM OportunidadLaboral o " +
            "JOIN o.etiquetas e " +
            "WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombreEtiqueta, '%')) " +
            "AND o.activa = true")
    List<OportunidadLaboral> buscarPorEtiqueta(@Param("nombreEtiqueta") String nombreEtiqueta);

    // Antes: "o.etiquetas.memberOf(Etiqueta)" era JPQL inválido.
    // @EntityGraph queda sólo en "autor" porque "etiquetas" ya se trae con JOIN FETCH.
    /**
     * Variante con {@code JOIN FETCH} de las etiquetas (evita el
     * acceso diferido al mapear), {@code DISTINCT} sobre la oferta y solo activas.
     */
    @EntityGraph(attributePaths = {"autor"})
    @Query("SELECT DISTINCT o FROM OportunidadLaboral o " +
            "JOIN FETCH o.etiquetas e " +
            "WHERE e.nombre IN :etiquetas AND o.activa = true")
    List<OportunidadLaboral> findByEtiquetaIn(@Param("etiquetas") List<String> etiquetas);

    //  Filtros varios

    /**
     * Ofertas activas por modalidad, comparada sin distinguir
     * mayúsculas; sin {@code DISTINCT}, por lo que el {@code EntityGraph} sobre
     * {@code etiquetas} puede devolver la misma oferta repetida.
     */
    @EntityGraph(attributePaths = {"etiquetas", "autor"})
    @Query("SELECT o FROM OportunidadLaboral o WHERE " +
            "LOWER(o.modalidad) = LOWER(:modalidad) " +
            "AND o.activa = true")
    List<OportunidadLaboral> findByModalidad(@Param("modalidad") String modalidad);

    // Antes filtraba por "o.habilitado", campo que NO existe en esta entidad
    // (habilitado sólo está en Usuario). Se usa "activa".
    /**
     * Todas las ofertas activas, sin texto ni etiquetas (el
     * «habilitado» de la versión original era un campo inexistente en esta entidad).
     */
    @EntityGraph(attributePaths = {"etiquetas", "autor"})
    @Query("SELECT o FROM OportunidadLaboral o WHERE o.activa = true")
    List<OportunidadLaboral> findHabilitadas();
}