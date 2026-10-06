package com.al_lado.al_lado_project.repository.seguridad;

import com.al_lado.al_lado_project.entity.seguridad.TokenRevocado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TokenRevocadoRepository extends JpaRepository<TokenRevocado, Long> {

    boolean existsByJti(String jti);

    void deleteByExpiraBefore(LocalDateTime fecha);
}
