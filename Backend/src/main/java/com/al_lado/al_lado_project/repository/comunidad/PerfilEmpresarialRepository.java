package com.al_lado.al_lado_project.repository.comunidad;

import com.al_lado.al_lado_project.entity.comunidad.PerfilEmpresarial;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilEmpresarialRepository extends JpaRepository<PerfilEmpresarial, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<PerfilEmpresarial> {

    @EntityGraph(attributePaths = {"empleador", "supervisadoPor"})
    org.springframework.data.domain.Page<PerfilEmpresarial> findAll(org.springframework.data.jpa.domain.Specification<PerfilEmpresarial> spec, org.springframework.data.domain.Pageable pageable);

    @EntityGraph(attributePaths = {"empleador", "supervisadoPor"})
    Optional<PerfilEmpresarial> findByEmpleadorId(Long empleadorId);

    Optional<PerfilEmpresarial> findByCuit(String cuit);

    List<PerfilEmpresarial> findByVerificadoTrue();

    boolean existsByCuit(String cuit);
}