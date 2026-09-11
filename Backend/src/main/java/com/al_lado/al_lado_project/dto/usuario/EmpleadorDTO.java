package com.al_lado.al_lado_project.dto.usuario;

import com.al_lado.al_lado_project.dto.comunidad.PerfilEmpresarialDTO;
import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralResumenDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmpleadorDTO extends UsuarioDTO {

    private PerfilEmpresarialDTO perfilEmpresarial;
    private List<OportunidadLaboralResumenDTO> oportunidades = new ArrayList<>();
}