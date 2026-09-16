package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.PerfilEmpresarialDTO;

public interface PerfilEmpresarialService {
    PerfilEmpresarialDTO obtenerPorEmpleador(Long empleadorId);
    PerfilEmpresarialDTO guardar(Long empleadorId, PerfilEmpresarialDTO dto);
    PerfilEmpresarialDTO verificar(Long id, Long supervisorId);
}
