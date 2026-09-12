package com.al_lado.al_lado_project.repository.perfil;

import com.al_lado.al_lado_project.entity.perfil.PreferenciaNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreferenciaNotificacionRepository extends JpaRepository<PreferenciaNotificacion, Long> {

    Optional<PreferenciaNotificacion> findByTrabajadorId(Long trabajadorId);

    boolean existsByTrabajadorId(Long trabajadorId);
}