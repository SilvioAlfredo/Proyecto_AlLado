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

@Repository
public interface OportunidadLaboralRepository
        extends JpaRepository<OportunidadLaboral, Long>,
        JpaSpecificationExecutor<OportunidadLaboral> {

    //  Usadas por los services

    @EntityGraph(attributePaths = {"etiquetas", "autor"})
    List<OportunidadLaboral> findByActivaTrue();

    List<OportunidadLaboral> findByAutorId(Long autorId);

    List<OportunidadLaboral> findByAutorIdAndActiva(Long autorId, boolean activa);

    // RF-09: tendencias de etiquetas para el dashboard de administración
    @Query("SELECT e.nombre, COUNT(o) " +
            "FROM OportunidadLaboral o JOIN o.etiquetas e " +
            "GROUP BY e.nombre " +
            "ORDER BY COUNT(o) DESC")
    List<Object[]> findTendenciasPorEtiqueta();

    // RF-04, HU-01: límite de 3 ofertas/mes del plan Básico.
    // fechaCreacion es LocalDateTime, no LocalDate.
    long countByAutorIdAndActivaAndFechaCreacionBetween(Long autorId, boolean activa,
                                                        LocalDateTime start, LocalDateTime end);

    // Paginación

    Page<OportunidadLaboral> findAllByActivaTrue(Pageable pageable);

    Page<OportunidadLaboral> findByAutorIdAndActiva(Long autorId, boolean activa, Pageable pageable);

    // Antes era query derivada sobre la propiedad "etiqueta", que no existe
    // (el campo se llama "etiquetas"). Ahora es un @Query explícito.
    @Query(value = "SELECT DISTINCT o FROM OportunidadLaboral o " +
            "JOIN o.etiquetas e " +
            "WHERE e.nombre IN :etiquetas AND o.activa = true",
            countQuery = "SELECT COUNT(DISTINCT o) FROM OportunidadLaboral o " +
                    "JOIN o.etiquetas e " +
                    "WHERE e.nombre IN :etiquetas AND o.activa = true")
    Page<OportunidadLaboral> findByEtiquetaIn(@Param("etiquetas") List<String> etiquetas,
                                              Pageable pageable);

    @EntityGraph(attributePaths = {"etiquetas", "autor"})
    @Query("SELECT o FROM OportunidadLaboral o WHERE " +
            "(LOWER(o.titulo) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(o.contenido) LIKE LOWER(CONCAT('%', :filtro, '%'))) " +
            "AND o.activa = true")
    List<OportunidadLaboral> buscarActivasPorTermino(@Param("filtro") String filtro);

    // Búsqueda por etiqueta

    // Antes: query derivada sobre "etiquetaNombre", propiedad inexistente.
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
    @EntityGraph(attributePaths = {"autor"})
    @Query("SELECT DISTINCT o FROM OportunidadLaboral o " +
            "JOIN FETCH o.etiquetas e " +
            "WHERE e.nombre IN :etiquetas AND o.activa = true")
    List<OportunidadLaboral> findByEtiquetaIn(@Param("etiquetas") List<String> etiquetas);

    //  Filtros varios

    @EntityGraph(attributePaths = {"etiquetas", "autor"})
    @Query("SELECT o FROM OportunidadLaboral o WHERE " +
            "LOWER(o.modalidad) = LOWER(:modalidad) " +
            "AND o.activa = true")
    List<OportunidadLaboral> findByModalidad(@Param("modalidad") String modalidad);

    // Antes filtraba por "o.habilitado", campo que NO existe en esta entidad
    // (habilitado sólo está en Usuario). Se usa "activa".
    @EntityGraph(attributePaths = {"etiquetas", "autor"})
    @Query("SELECT o FROM OportunidadLaboral o WHERE o.activa = true")
    List<OportunidadLaboral> findHabilitadas();
}