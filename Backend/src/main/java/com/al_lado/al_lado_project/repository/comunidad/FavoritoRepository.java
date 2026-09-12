package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    List<Favorito> findByEmpleadorId(Long empleadorId);

    Optional<Favorito> findByEmpleadorIdAndTrabajadorId(Long empleadorId, Long trabajadorId);

    boolean existsByEmpleadorIdAndTrabajadorId(Long empleadorId, Long trabajadorId);

    void deleteByEmpleadorIdAndTrabajadorId(Long empleadorId, Long trabajadorId);
}