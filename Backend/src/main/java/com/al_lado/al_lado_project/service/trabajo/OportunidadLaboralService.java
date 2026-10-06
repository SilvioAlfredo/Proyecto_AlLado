package com.al_lado.al_lado_project.service.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.FiltrosOportunidadesDTO;
import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OportunidadLaboralService {
    List<OportunidadLaboralDTO> obtenerTodas();
    Page<OportunidadLaboralDTO> listarOportunidades(String filtro, Pageable pageable);
    OportunidadLaboralDTO obtenerPorId(Long id);
    OportunidadLaboralDTO guardar(OportunidadLaboralDTO oportunidadDTO);
    OportunidadLaboralDTO cerrarOportunidad(Long id);
    void eliminar(Long id);
    List<Trabajador> obtenerTrabajadoresPorEtiqueta(String nombreEtiqueta);

    // RF-02: búsqueda combinada de oportunidades. Todos los criterios son opcionales
    // y se acumulan (AND entre ellos, OR dentro del texto libre). Implementado con
    // Criteria API en OportunidadLaboralServiceImpl, reutilizando la misma
    // Specification que usa listarOportunidades().
    List<OportunidadLaboralDTO> buscarPorFiltros(String filtro, String modalidad, List<String> etiquetas);

    // RF-06 / HU-06: Listar oportunidades con filtros completos (paginación incluida)
    List<OportunidadLaboralDTO> listarConFiltros(FiltrosOportunidadesDTO filtros);
}