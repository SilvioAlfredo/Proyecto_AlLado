package com.al_lado.al_lado_project.repository.perfil;

import com.al_lado.al_lado_project.entity.perfil.PortafolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PortafolioItemRepository extends JpaRepository<PortafolioItem, Long> {

    List<PortafolioItem> findByTrabajadorId(Long trabajadorId);

    @Modifying
    @Transactional
    void deleteByTrabajadorId(Long trabajadorId);
}