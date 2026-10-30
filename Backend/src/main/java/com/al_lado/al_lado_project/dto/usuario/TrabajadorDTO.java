package com.al_lado.al_lado_project.dto.usuario;

import com.al_lado.al_lado_project.dto.perfil.HorarioDisponibilidadDTO;
import com.al_lado.al_lado_project.dto.perfil.PortafolioItemDTO;
import com.al_lado.al_lado_project.dto.perfil.PreferenciaNotificacionDTO;
import com.al_lado.al_lado_project.dto.trabajo.EtiquetaDTO;
import com.al_lado.al_lado_project.dto.trabajo.PostulacionResumenDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

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
    private String fotografia; // RF-02, HU-03: campo solicitado en la spec
    private String biografia; // RF-02, HU-03: campo solicitado en la spec
    private String certificados; // RF-02, HU-03: campo solicitado en la spec
    private String sector; // HU-13 CA-02, RF-13: sector barrial o zona
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitud;
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitud;

    @AssertTrue(message = "Debe indicar latitud y longitud juntas.")
    public boolean isParCoordenadasValido() {
        return (latitud == null) == (longitud == null);
    }
}