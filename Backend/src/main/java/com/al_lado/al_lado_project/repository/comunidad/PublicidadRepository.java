package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Publicidad;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio de publicidades.
 *
 * <p>La consulta relevante para el negocio es {@link #buscarPublicidadesVigentes(LocalDate)}:
 * las demás ({@link #findByActivaTrue()}) no tienen llamadores.</p>
 */
@Repository
public interface PublicidadRepository extends JpaRepository<Publicidad, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Publicidad> {

    @EntityGraph(attributePaths = {"anunciante"})
    org.springframework.data.domain.Page<Publicidad> findAll(org.springframework.data.jpa.domain.Specification<Publicidad> spec, org.springframework.data.domain.Pageable pageable);

    @EntityGraph(attributePaths = {"anunciante"})
    List<Publicidad> findByAnuncianteId(Long anuncianteId);


    List<Publicidad> findByActivaTrue();

    /**
     * Publicidades a mostrar hoy: {@code activa=true} y fecha actual dentro del
     * rango, mediante {@code BETWEEN}, que es <b>inclusivo en ambos extremos</b>
     * ({@code inicio ≤ hoy ≤ fin}): el día de inicio y el de fin se muestran.
     *
     * <p>No filtra por anunciante ni por cualquier otra condición; los resultados se
     * mapean con el anunciante precargado ({@code @EntityGraph}) para evitar
     * accesos diferidos.</p>
     *
     * @param fechaActual fecha a evaluar (quien llama al método envía {@code LocalDate.now()})
     */
    @EntityGraph(attributePaths = {"anunciante"})
    @Query("SELECT p FROM Publicidad p WHERE p.activa = true AND :fechaActual BETWEEN p.fechaInicio AND p.fechaFin")
    List<Publicidad> buscarPublicidadesVigentes(@Param("fechaActual") LocalDate fechaActual);
}