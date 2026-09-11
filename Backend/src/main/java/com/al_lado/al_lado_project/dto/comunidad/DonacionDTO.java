package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonacionDTO {

    private Long id;
    private BigDecimal monto;
    private String moneda;
    private String mecanismoPago;
    private LocalDateTime fechaDonacion;
    private UsuarioResumenDTO usuario;
}