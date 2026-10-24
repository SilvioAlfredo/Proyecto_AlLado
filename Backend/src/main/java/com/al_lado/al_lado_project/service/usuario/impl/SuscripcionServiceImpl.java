package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.usuario.SuscripcionMapper;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuscripcionServiceImpl implements SuscripcionService {

    private final SuscripcionRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final OportunidadLaboralRepository oportunidadLaboralRepository;
    private final SuscripcionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SuscripcionDTO obtenerPorId(Long id) {
        Suscripcion suscripcion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripción no encontrada con ID: " + id));
        return mapper.toDTO(suscripcion);
    }

    @Override
    @Transactional(readOnly = true)
    public SuscripcionDTO obtenerPorUsuario(Long usuarioId) {
        SecurityUtils.requireSelfOrAdmin(usuarioId);
        Suscripcion suscripcion = repository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no tiene una suscripción cargada."));
        return mapper.toDTO(suscripcion);
    }

    @Override
    @Transactional
    public SuscripcionDTO crear(SuscripcionDTO dto) {
        if (dto.getUsuario() == null || dto.getUsuario().getId() == null) {
            throw new BusinessRuleException("El usuario es obligatorio para crear una suscripción.");
        }

        Long usuarioId = dto.getUsuario().getId();
        SecurityUtils.requireSelfOrAdmin(usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // AL-SEC-007 (MEJ-002): el nivel PRO solo lo concede un administrador, tras
        // verificar el aporte. Un usuario no puede auto-otorgarse PRO ni cambiar un
        // plan existente a PRO por su cuenta.
        boolean admin = SecurityUtils.isAdmin();
        String nivelSolicitado = dto.getNivel() != null ? dto.getNivel().toUpperCase() : "BASICO";
        if (!"BASICO".equals(nivelSolicitado) && !"PRO".equals(nivelSolicitado)) {
            throw new BusinessRuleException("Nivel inválido. Valores permitidos: BASICO, PRO.");
        }
        if ("PRO".equals(nivelSolicitado) && !admin) {
            throw new AccessDeniedException(
                    "El plan PRO solo puede activarlo un administrador después de verificar el aporte.");
        }

        if (repository.existsByUsuarioId(usuarioId)) {
            // Sincronía con el frontend ("Mi plan" re-activa PRO): en lugar de
            // rechazar con duplicado se reactiva la suscripción existente.
            Suscripcion existente = repository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Suscripción no encontrada para el usuario con ID: " + usuarioId));
            SecurityUtils.requireSelfOrAdmin(usuarioId);
            existente.setNivel(nivelSolicitado);
            // Sin ADMIN, el alta propia es siempre un plan básico sin vencimiento:
            // las fechas las fija el administrador al conceder PRO.
            existente.setFechaInicio(admin && dto.getFechaInicio() != null
                    ? dto.getFechaInicio() : LocalDate.now());
            existente.setFechaFin(admin ? dto.getFechaFin() : null);
            existente.setActiva(true);
            return mapper.toDTO(repository.save(existente));
        }

        Suscripcion suscripcion = mapper.toEntity(dto);
        suscripcion.setUsuario(usuario);
        suscripcion.setNivel(nivelSolicitado);
        suscripcion.setFechaInicio(admin && dto.getFechaInicio() != null
                ? dto.getFechaInicio() : LocalDate.now());
        suscripcion.setFechaFin(admin ? dto.getFechaFin() : null);
        suscripcion.setActiva(true);

        return mapper.toDTO(repository.save(suscripcion));
    }

    @Override
    @Transactional
    public SuscripcionDTO cancelar(Long id) {
        Suscripcion suscripcion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripción no encontrada con ID: " + id));

        if (suscripcion.getUsuario() == null || suscripcion.getUsuario().getId() == null) {
            throw new ResourceNotFoundException("Suscripción sin usuario asociado.");
        }
        SecurityUtils.requireSelfOrAdmin(suscripcion.getUsuario().getId());

        suscripcion.setActiva(false);
        return mapper.toDTO(repository.save(suscripcion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Suscripción no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Cuenta ofertas activas creadas en el mes actual.
     *
     * <p>El período va del día 1 a las 00:00:00 al último día a las 23:59:59. El bloqueo del
     * empleador se adquiere al guardar una oferta; este método no lo adquiere por separado.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public boolean puedePublicarOferta(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Suscripcion suscripcion = repository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    // El usuario no tiene suscripción -> es Nivel Básico (limitado a 3/mes)
                    return null;
                });

        if (suscripcion == null || suscripcion.getNivel() == null
                || !suscripcion.isActiva() || estaVencida(suscripcion)
                || "BASICO".equalsIgnoreCase(suscripcion.getNivel())) {
            // Nivel Básico (o PRO inactiva/vencida): límite de 3 ofertas por mes
            long ofertasMes = contarOfertasPorMes(usuarioId, suscripcion);
            return ofertasMes < 3;
        } else {
            // Nivel Pro vigente: ofertas ilimitadas
            return true;
        }
    }

    private boolean estaVencida(Suscripcion suscripcion) {
        return suscripcion.getFechaFin() != null && suscripcion.getFechaFin().isBefore(LocalDate.now());
    }

    /**
     * Comprueba nivel PRO, activa y fecha de fin.
     *
     * <p>Una fecha de fin igual a hoy sigue vigente. No comprueba fechaInicio.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public boolean esProVigente(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .filter(s -> "PRO".equalsIgnoreCase(s.getNivel()))
                .filter(Suscripcion::isActiva)
                .filter(s -> !estaVencida(s))
                .isPresent();
    }

    private long contarOfertasPorMes(Long usuarioId, Suscripcion suscripcion) {
        // Contar ofertas publicadas en el mes actual
        YearMonth currentMonth = YearMonth.now();
        return oportunidadLaboralRepository.countByAutorIdAndActivaAndFechaCreacionBetween(
                usuarioId, true,
                currentMonth.atDay(1).atStartOfDay(),
                // YearMonth no tiene atEndOfDay(): se toma el último día del mes
                currentMonth.atEndOfMonth().atTime(23, 59, 59));
    }
}