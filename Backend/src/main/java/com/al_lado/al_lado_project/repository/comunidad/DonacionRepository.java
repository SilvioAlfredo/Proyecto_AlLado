package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Donacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> {

    List<Donacion> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {"usuario"})
    Page<Donacion> findByUsuarioId(Long usuarioId, Pageable pageable);

    @EntityGraph(attributePaths = {"usuario"})
    Page<Donacion> findAllByOrderByFechaDonacionDesc(Pageable pageable);

    // Agregacion en BD: evita cargar todas las donaciones para sumar.
    // BigDecimal puro, sin paso por double (precision monetaria).
    @Query("SELECT SUM(d.monto) FROM Donacion d")
    BigDecimal sumTotalMontos();
}