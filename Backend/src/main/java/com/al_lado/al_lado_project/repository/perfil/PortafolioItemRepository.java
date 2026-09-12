package com.al_lado.al_lado_project.repository.perfil;

import com.al_lado.al_lado_project.entity.perfil.PortafolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortafolioItemRepository extends JpaRepository<PortafolioItem, Long> {

    List<PortafolioItem> findByTrabajadorId(Long trabajadorId);

    void deleteByTrabajadorId(Long trabajadorId);
}