package com.al_lado.al_lado_project.dto.usuario;

import com.al_lado.al_lado_project.dto.perfil.HorarioDisponibilidadDTO;
import com.al_lado.al_lado_project.dto.perfil.PortafolioItemDTO;
import com.al_lado.al_lado_project.dto.perfil.PreferenciaNotificacionDTO;
import com.al_lado.al_lado_project.dto.trabajo.EtiquetaDTO;
import com.al_lado.al_lado_project.dto.trabajo.PostulacionResumenDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrabajadorDTO extends UsuarioDTO {

    private List<PostulacionResumenDTO> postulaciones = new ArrayList<>();
    private Set<EtiquetaDTO> especialidades = new HashSet<>();
    private List<PortafolioItemDTO> portafolioItems = new ArrayList<>();
    private List<HorarioDisponibilidadDTO> horarios = new ArrayList<>();
    private PreferenciaNotificacionDTO preferenciaNotificacion;
}