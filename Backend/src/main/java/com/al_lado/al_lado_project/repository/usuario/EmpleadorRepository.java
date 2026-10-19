package com.al_lado.al_lado_project.repository.usuario;

import com.al_lado.al_lado_project.entity.usuario.Empleador;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de empleadores.
 *
 * <p>Aparte de los accesos estándar, expone una carga con
 * {@link jakarta.persistence.LockModeType#PESSIMISTIC_WRITE} usada para serializar
 * operaciones concurrentes del mismo propietario.</p>
 */
@Repository
public interface EmpleadorRepository extends JpaRepository<Empleador, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Empleador> {

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"perfilEmpresarial", "perfilEmpresarial.supervisadoPor"})
    org.springframework.data.domain.Page<Empleador> findAll(org.springframework.data.jpa.domain.Specification<Empleador> spec, org.springframework.data.domain.Pageable pageable);

    Optional<Empleador> findByEmail(String email);

    /**
     * Carga el empleador con <b>bloqueo pesimista de escritura</b> para serializar
     * creaciones concurrentes del mismo empleador (p. ej. el control de límite de
     * ofertas en {@code OportunidadLaboralServiceImpl#guardar}).
     *
     * <p>El lock se adquiere sobre la fila del empleador y <b>se retiene hasta que
     * termina la transacción</b> que lo pidió: por eso los llamadores
     * ({@code OportunidadLaboralServiceImpl#guardar}) son {@code @Transactional}. Solo
     * serializa operaciones que bloquean <b>el mismo empleador</b>; ofertas de
     * empleadores distintos no comparten bloqueo.</p>
     *
     * @param id ID del empleador
     * @return el empleador bloqueado, o vacío si no existe (no bloquea nada)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Empleador e WHERE e.id = :id")
    Optional<Empleador> findByIdForUpdate(@Param("id") Long id);
}