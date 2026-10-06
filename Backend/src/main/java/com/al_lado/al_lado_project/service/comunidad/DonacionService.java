package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DonacionService {
    Page<DonacionDTO> obtenerTodas(Pageable pageable);
    DonacionDTO obtenerPorId(Long id);
    Page<DonacionDTO> obtenerPorUsuario(Long usuarioId, Pageable pageable);
    DonacionDTO crear(DonacionDTO dto);
    DonacionDTO verificarTransferencia(Long id, String asuntoEsperado, BigDecimal montoEsperado);
}
