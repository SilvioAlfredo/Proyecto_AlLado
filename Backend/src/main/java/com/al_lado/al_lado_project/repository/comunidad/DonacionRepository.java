package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Donacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio de donaciones y suscripciones por transferencia.
 *
 * <p>Reúne el lock de verificación ({@link #findByIdForUpdate(Long)}) y la
 * agregación de ingresos del dashboard ({@link #sumTotalMontos()}).</p>
 */
@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long>, JpaSpecificationExecutor<Donacion> {

    @EntityGraph(attributePaths = {"usuario"})
    Page<Donacion> findAll(Specification<Donacion> specification, Pageable pageable);

    /**
     * Carga la donación con <b>bloqueo pesimista de escritura</b>; lo usa
     * {@code DonacionServiceImpl#verificarTransferencia} para que dos verificaciones
     * simultáneas <b>del mismo registro</b> queden serializadas y solo la primera
     * pueda sacarla de {@code PENDIENTE}.
     *
     * <p>El lock dura hasta que termina la transacción dquien llama al método
     * ({@code DonacionServiceImpl#verificarTransferencia} es {@code @Transactional}).
     * <b>No</b> serializa verificaciones de donaciones distintas, ni tampoco dos
     * donaciones distintas del mismo usuario.</p>
     *
     * @param id ID de la donación
     * @return la donación bloqueada, o vacío si no existe
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Donacion d WHERE d.id = :id")
    Optional<Donacion> findByIdForUpdate(@Param("id") Long id);

    List<Donacion> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {"usuario"})
    Page<Donacion> findByUsuarioId(Long usuarioId, Pageable pageable);

    @EntityGraph(attributePaths = {"usuario"})
    Page<Donacion> findAllByOrderByFechaDonacionDesc(Pageable pageable);

    /**
     * Suma de <b>todas</b> las donaciones, sin filtrar por estado: incluye las que
     * siguen {@code PENDIENTE} y las descartadas, por lo que el resultado del
     * dashboard no es ingreso cobrado.
     *
     * <p>Agregación en BD (evita cargar las entidades) y en {@code BigDecimal}, sin
     * pasar por {@code double}. Devuelve {@code null} si no hay filas; quien llama al método lo
     * sustituye por cero.</p>
     */
    @Query("SELECT SUM(d.monto) FROM Donacion d")
    BigDecimal sumTotalMontos();
}