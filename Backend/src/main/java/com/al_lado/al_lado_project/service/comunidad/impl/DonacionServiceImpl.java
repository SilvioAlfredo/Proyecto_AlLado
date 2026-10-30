package com.al_lado.al_lado_project.service.comunidad.impl;

import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Donacion;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.comunidad.DonacionMapper;
import com.al_lado.al_lado_project.repository.comunidad.DonacionRepository;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.DonacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DonacionServiceImpl implements DonacionService {

    private final DonacionRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final DonacionMapper mapper;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public Page<DonacionDTO> obtenerTodas(Pageable pageable) {
        return repository.findAllByOrderByFechaDonacionDesc(pageable).map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public DonacionDTO obtenerPorId(Long id) {
        Donacion donacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donación no encontrada con ID: " + id));
        Long propietarioId = donacion.getUsuario() != null ? donacion.getUsuario().getId() : null;
        SecurityUtils.requireSelfOrAdmin(propietarioId);
        return mapper.toDTO(donacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DonacionDTO> obtenerPorUsuario(Long usuarioId, Pageable pageable) {
        SecurityUtils.requireSelfOrAdmin(usuarioId);
        return repository.findByUsuarioId(usuarioId, pageable).map(mapper::toDTO);
    }

    @Override
    @Transactional
    public DonacionDTO crear(DonacionDTO dto) {
        if (dto.getUsuario() == null || dto.getUsuario().getId() == null) {
            throw new BusinessRuleException("El usuario es obligatorio para registrar una donación o suscripción.");
        }
        if (dto.getMonto() == null || dto.getMonto().signum() <= 0) {
            throw new BusinessRuleException("El monto de la donación o suscripción debe ser mayor a cero.");
        }

        Long usuarioId = dto.getUsuario().getId();
        SecurityUtils.requireSelfOrAdmin(usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Donacion donacion = mapper.toEntity(dto);
        donacion.setUsuario(usuario);

        // Estado inicial siempre PENDIENTE de verificación admin: ignora
        // cualquier estado enviado por el cliente en el alta.
        donacion.setEstado("PENDIENTE");

        // Por defecto, las donaciones/suscripciones por transferencia están PENDIENTES de verificación.
        // Se incluye "SUSCRIPCION": es el caso de una suscripción paga por transferencia,
        // que también necesita conservar los datos bancarios para que el admin pueda
        // verificarla luego en verificarTransferencia().
        if ("TRANSFERENCIA".equals(dto.getMecanismoPago()) || "SUSCRIPCION".equals(dto.getMecanismoPago())) {
            donacion.setEstado("PENDIENTE");
            donacion.setBanco(dto.getBanco());
            donacion.setTipoTransferencia(dto.getTipoTransferencia());
            donacion.setAsuntoTransferencia(dto.getAsuntoTransferencia());
        }

        return mapper.toDTO(repository.save(donacion));
    }

    /**
     * Verifica el aporte dentro de una transacción con bloqueo pesimista de su fila.
     *
     * <p>El bloqueo se mantiene hasta finalizar la transacción y protege solo ese aporte. La
     * suscripción se guarda por separado: no se persiste mediante la cascada de la donación. Si
     * la comparación falla, el registro no cambia.</p>
     */
    @Override
    @Transactional
    public DonacionDTO verificarTransferencia(Long id, String asuntoEsperado, BigDecimal montoEsperado) {
        Donacion donacion = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donación no encontrada con ID: " + id));

        if (!"PENDIENTE".equals(donacion.getEstado())) {
            throw new BusinessRuleException("Esta donación/suscripción ya ha sido procesada.");
        }

        // Verificar que el asunto y monto coincidan (comparaciones null-safe:
        // un parámetro esperado nulo nunca coincide, no lanza NPE)
        if (donacion.getAsuntoTransferencia() == null || asuntoEsperado == null
                || !donacion.getAsuntoTransferencia().equals(asuntoEsperado)) {
            throw new BusinessRuleException("El asunto de la transferencia no coincide. Verifique el registro bancario.");
        }

        // compareTo ignora la escala (100.00 == 100.0); equals la rechazaría.
        if (donacion.getMonto() == null || montoEsperado == null || donacion.getMonto().compareTo(montoEsperado) != 0) {
            throw new BusinessRuleException("El monto de la transferencia no coincide. Verifique el registro bancario.");
        }

        // Asignar suscripción o marcar como verificado
        donacion.setEstado("VERIFICADO");

        // Si es una suscripción, activarla para el usuario
        if ("SUSCRIPCION".equals(donacion.getMecanismoPago())) {
            Long usuarioId = donacion.getUsuario() != null && donacion.getUsuario().getId() != null
                    ? donacion.getUsuario().getId()
                    : null;
            if (usuarioId == null) {
                throw new BusinessRuleException("La donación no tiene un usuario asociado.");
            }
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

            // Crear o actualizar suscripción a Nivel PRO. Se persiste de forma
            // explícita: save(donacion) no cascada hasta Usuario.suscripcion
            // (el cascade está en el lado inverso mappedBy).
            Suscripcion suscripcion = usuario.getSuscripcion();
            if (suscripcion == null) {
                LocalDate inicio = LocalDate.now(clock);
                suscripcion = new Suscripcion(
                        usuario,
                        "PRO", // Nivel Pro por transferencia exitosa
                        inicio,
                        inicio.plusYears(1),
                        true
                );
            } else {
                suscripcion.setNivel("PRO");
                LocalDate inicio = LocalDate.now(clock);
                suscripcion.setFechaInicio(inicio);
                suscripcion.setFechaFin(inicio.plusYears(1));
                suscripcion.setActiva(true);
            }
            suscripcionRepository.save(suscripcion);
            usuario.setSuscripcion(suscripcion);
        }

        return mapper.toDTO(repository.save(donacion));
    }
}