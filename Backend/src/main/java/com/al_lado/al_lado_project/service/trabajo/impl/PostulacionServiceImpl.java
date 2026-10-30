package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Notificacion;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionMapper;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.comunidad.ValoracionRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.comunidad.NotificacionService;
import com.al_lado.al_lado_project.service.trabajo.PostulacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.al_lado.al_lado_project.dto.usuario.EstadisticasTrabajadorDTO;
import java.math.BigDecimal;
import com.al_lado.al_lado_project.service.usuario.SuscripcionVigencia;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Aplica los cambios de estado y sus notificaciones en una transacción.
 *
 * <p>No usa bloqueo de fila para estos cambios. Dos solicitudes simultáneas
 * pueden leer el mismo estado antes de actualizarlo.</p>
 */
@Service
@RequiredArgsConstructor
public class PostulacionServiceImpl implements PostulacionService {

    private final PostulacionRepository repository;
    private final TrabajadorRepository trabajadorRepository;
    private final OportunidadLaboralRepository oportunidadRepository;
    private final ValoracionRepository valoracionRepository;
    private final PostulacionMapper mapper;
    private final NotificacionService notificacionService;

    /**
     * Alcance de lectura de {@code GET /postulaciones} (AL-SEC-006).
     *
     * <p>ADMIN ve todas las del sistema; un trabajador, las suyas; un empleador, las
     * recibidas por sus ofertas. Quien no tenga una de esas relaciones no tiene nada
     * que ver aquí y recibe 403 en lugar del listado completo de terceros.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<PostulacionDTO> obtenerTodas() {
        if (SecurityUtils.isAdmin()) {
            return repository.findAll().stream()
                    .map(mapper::toDTO)
                    .collect(Collectors.toList());
        }

        Usuario actual = SecurityUtils.currentUser();
        if (actual == null) {
            throw new AccessDeniedException("No tiene permisos para consultar postulaciones.");
        }

        List<Postulacion> alcance = actual instanceof Empleador
                ? repository.findByOportunidadLaboralAutorId(actual.getId())
                : repository.findByAutorId(actual.getId());

        return alcance.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Una postulación sólo es visible para su autor, el empleador dueño de la oferta
     * o un administrador (AL-SEC-006); para el resto responde 403, sin importar si la
     * postulación existe.
     */
    @Override
    @Transactional(readOnly = true)
    public PostulacionDTO obtenerPorId(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulación no encontrada con ID: " + id));
        exigirAlcanceDeLectura(postulacion);
        return mapper.toDTO(postulacion);
    }

    /** Título/ADMIN pueden leer la postulación; el resto no. */
    private void exigirAlcanceDeLectura(Postulacion postulacion) {
        if (SecurityUtils.isAdmin()) {
            return;
        }

        Long actual = SecurityUtils.currentUserId();
        if (actual == null) {
            throw new AccessDeniedException("No tiene permisos para consultar esta postulación.");
        }

        Long autorId = postulacion.getAutor() != null ? postulacion.getAutor().getId() : null;
        OportunidadLaboral oportunidad = postulacion.getOportunidadLaboral();
        Long empleadorId = oportunidad != null && oportunidad.getAutor() != null
                ? oportunidad.getAutor().getId() : null;

        if (actual.equals(autorId) || actual.equals(empleadorId)) {
            return;
        }
        throw new AccessDeniedException("No tiene permisos para consultar esta postulación.");
    }

    @Override
    @Transactional
    public PostulacionDTO crearPostulacion(PostulacionDTO dto) {
        if (dto.getAutor() == null || dto.getAutor().getId() == null) {
            throw new BusinessRuleException("El autor (Trabajador) es obligatorio.");
        }
        if (dto.getOportunidadLaboral() == null || dto.getOportunidadLaboral().getId() == null) {
            throw new BusinessRuleException("La oportunidad laboral es obligatoria.");
        }

        Long trabajadorId = dto.getAutor().getId();
        SecurityUtils.requireSelfOrAdmin(trabajadorId);
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + trabajadorId));

        Long oportunidadId = dto.getOportunidadLaboral().getId();
        OportunidadLaboral oportunidad = oportunidadRepository.findById(oportunidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidad laboral no encontrada con ID: " + oportunidadId));

        if (!oportunidad.isActiva()) {
            throw new BusinessRuleException("No se permiten postulaciones a una oportunidad laboral cerrada.");
        }

        if (repository.existsByOportunidadLaboralIdAndAutorId(oportunidadId, trabajadorId)) {
            throw new DuplicateResourceException("El trabajador ya se encuentra postulado a esta oportunidad laboral.");
        }

        Postulacion postulacion = mapper.toEntity(dto);
        postulacion.setAutor(trabajador);
        postulacion.setOportunidadLaboral(oportunidad);
        postulacion.setEstado(Postulacion.ESTADO_PENDIENTE);

        Postulacion guardada = repository.save(postulacion);

        // HU: avisar al empleador dueño de la oferta
        if (oportunidad.getAutor() != null && oportunidad.getAutor().getId() != null) {
            notificacionService.notificar(
                    oportunidad.getAutor().getId(),
                    "Nueva postulación recibida",
                    trabajador.getNombre() + " se postuló a tu oferta \"" + oportunidad.getTitulo() + "\".",
                    Notificacion.TIPO_POSTULACION_RECIBIDA,
                    guardada.getId());
        }

        return mapper.toDTO(guardada);
    }

    @Override
    @Transactional
    public PostulacionDTO seleccionarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulación no encontrada con ID: " + id));

        exigirEmpleadorPropietario(postulacion);
        if (!Postulacion.ESTADO_PENDIENTE.equals(postulacion.getEstado())) {
            throw new BusinessRuleException("Solo las postulaciones PENDIENTES pueden seleccionarse.");
        }

        postulacion.setEstado(Postulacion.ESTADO_SELECCIONADA);
        Postulacion guardada = repository.save(postulacion);
        notificarCambioEstado(guardada, "Tu postulación fue seleccionada");
        return mapper.toDTO(guardada);
    }

    @Override
    @Transactional
    public PostulacionDTO completarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulación no encontrada con ID: " + id));

        exigirEmpleadorPropietario(postulacion);
        // RF-08/HU-08: solo se puede completar una postulacion seleccionada
        if (!Postulacion.ESTADO_SELECCIONADA.equals(postulacion.getEstado())) {
            throw new BusinessRuleException("Solo las postulaciones con estado SELECCIONADA pueden completarse.");
        }
        postulacion.setEstado(Postulacion.ESTADO_COMPLETADA);
        Postulacion guardada = repository.save(postulacion);
        notificarCambioEstado(guardada, "Tu postulación fue marcada como completada");
        return mapper.toDTO(guardada);
    }

    @Override
    @Transactional
    public PostulacionDTO rechazarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulación no encontrada con ID: " + id));

        exigirEmpleadorPropietario(postulacion);
        if (!Postulacion.ESTADO_PENDIENTE.equals(postulacion.getEstado())
                && !Postulacion.ESTADO_SELECCIONADA.equals(postulacion.getEstado())) {
            throw new BusinessRuleException("Solo las postulaciones PENDIENTES o SELECCIONADAS pueden rechazarse.");
        }

        postulacion.setEstado(Postulacion.ESTADO_RECHAZADA);
        Postulacion guardada = repository.save(postulacion);
        notificarCambioEstado(guardada, "Tu postulación fue rechazada");
        return mapper.toDTO(guardada);
    }

    /** Avisa al trabajador autor cuando su postulación cambia de estado. */
    private void notificarCambioEstado(Postulacion postulacion, String titulo) {
        // Nota: no se usa getTrabajador() porque el autor llega como proxy
        // lazy de Usuario y el instanceof fallaría. El autor de una
        // postulación es siempre un trabajador (ver crearPostulacion).
        if (postulacion.getAutor() != null && postulacion.getAutor().getId() != null
                && postulacion.getOportunidadLaboral() != null) {
            notificacionService.notificar(
                    postulacion.getAutor().getId(),
                    titulo,
                    titulo + " en la oferta \"" + postulacion.getOportunidadLaboral().getTitulo() + "\".",
                    Notificacion.TIPO_POSTULACION_ESTADO,
                    postulacion.getId());
        }
    }

    @Override
    @Transactional
    public PostulacionDTO retirarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulación no encontrada con ID: " + id));

        // HU trabajador: solo el autor puede retirar su postulación, y solo
        // mientras siga abierta (PENDIENTE o SELECCIONADA). Se usa getAutor()
        // en vez de getTrabajador(): el proxy lazy de Usuario rompería el instanceof.
        Long autorId = postulacion.getAutor() != null ? postulacion.getAutor().getId() : null;
        SecurityUtils.requireSelfOrAdmin(autorId);
        if (autorId == null || !trabajadorRepository.existsById(autorId)) {
            throw new BusinessRuleException("Solo el trabajador autor puede retirar su postulación.");
        }
        if (!Postulacion.ESTADO_PENDIENTE.equals(postulacion.getEstado())
                && !Postulacion.ESTADO_SELECCIONADA.equals(postulacion.getEstado())) {
            throw new BusinessRuleException("Solo las postulaciones PENDIENTES o SELECCIONADAS pueden retirarse.");
        }

        postulacion.setEstado(Postulacion.ESTADO_RETIRADA);
        Postulacion guardada = repository.save(postulacion);
        if (postulacion.getOportunidadLaboral() != null
                && postulacion.getOportunidadLaboral().getAutor() != null
                && postulacion.getOportunidadLaboral().getAutor().getId() != null) {
            notificacionService.notificar(
                    postulacion.getOportunidadLaboral().getAutor().getId(),
                    "Un trabajador retiró su postulación",
                    "Se retiró una postulación de tu oferta \"" + postulacion.getOportunidadLaboral().getTitulo() + "\".",
                    Notificacion.TIPO_POSTULACION_ESTADO,
                    guardada.getId());
        }
        return mapper.toDTO(guardada);
    }

    /** Solo el empleador dueño de la oferta (o un admin) opera sus postulaciones. */
    private void exigirEmpleadorPropietario(Postulacion postulacion) {        OportunidadLaboral oportunidad = postulacion.getOportunidadLaboral();
        if (oportunidad == null || oportunidad.getAutor() == null || oportunidad.getAutor().getId() == null) {
            throw new BusinessRuleException("La postulación no tiene una oportunidad válida asociada.");
        }
        SecurityUtils.requireSelfOrAdmin(oportunidad.getAutor().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticasTrabajadorDTO obtenerEstadisticasTrabajador(Long trabajadorId) {
        SecurityUtils.requireSelfOrAdmin(trabajadorId);
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + trabajadorId));
        return construirEstadisticas(trabajador);
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticasTrabajadorDTO obtenerEstadisticasTrabajadorPorEmail(String email) {
        // Mantener misma respuesta exista o no el email, para impedir enumerar cuentas.
        Trabajador trabajador = trabajadorRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("No tiene permisos para consultar estas estadísticas."));
        SecurityUtils.requireSelfOrAdmin(trabajador.getId());
        return construirEstadisticas(trabajador);
    }

    private EstadisticasTrabajadorDTO construirEstadisticas(Trabajador trabajador) {
        Long id = trabajador.getId();
        long completadas = repository.countByAutorIdAndEstadoIgnoreCase(id, Postulacion.ESTADO_COMPLETADA);
        long seleccionadas = repository.countByAutorIdAndEstadoIgnoreCase(id, Postulacion.ESTADO_SELECCIONADA);
        long enviadas = repository.countByAutorId(id);
        Long cantidadValoraciones = valoracionRepository.countByTrabajadorId(id);
        Double promedioValoraciones = valoracionRepository.calcularPromedioPuntuacionPorTrabajadorId(id);
        BigDecimal promedio = promedioValoraciones == null ? BigDecimal.ZERO : BigDecimal.valueOf(promedioValoraciones);

        LocalDate hoy = LocalDate.now();
        Suscripcion suscripcion = trabajador.getSuscripcion();
        boolean suscripcionVigente = suscripcion != null && SuscripcionVigencia.esVigente(suscripcion, hoy);
        boolean ingresosDisponibles = suscripcionVigente
                && "PRO".equalsIgnoreCase(suscripcion.getNivel());

        BigDecimal ingresosTotales = BigDecimal.ZERO;
        BigDecimal ingresosUltimos3Meses = BigDecimal.ZERO;
        if (ingresosDisponibles) {
            Double total = repository.sumarImportesCompletadosPorTrabajador(id);
            Double ultimosTresMeses = repository.sumarImportesCompletadosPorTrabajadorDesde(
                    id, hoy.minusMonths(3).atStartOfDay());
            ingresosTotales = BigDecimal.valueOf(total == null ? 0.0 : total);
            ingresosUltimos3Meses = BigDecimal.valueOf(ultimosTresMeses == null ? 0.0 : ultimosTresMeses);
        }

        EstadisticasTrabajadorDTO stats = new EstadisticasTrabajadorDTO();
        stats.setId(id);
        stats.setNombre(trabajador.getNombre());
        stats.setEmail(trabajador.getEmail());
        stats.setIngresosTotales(ingresosTotales);
        stats.setIngresosUltimos3Meses(ingresosUltimos3Meses);
        stats.setIngresosDisponibles(ingresosDisponibles);
        stats.setValoracionesCount(cantidadValoraciones == null ? 0 : cantidadValoraciones.intValue());
        stats.setPromedioValoraciones(promedio);
        stats.setOfertasPublicadas(0);
        stats.setPostulacionesEnviadas((int) enviadas);
        stats.setPostulacionesSeleccionadas((int) seleccionadas);
        stats.setPostulacionesCompletadas((int) completadas);
        stats.setNivelRecomendacion(4);
        stats.setPerfilCompleto(trabajador.getFotografia() != null && !trabajador.getFotografia().isEmpty());
        stats.setUltimoLogin(null);
        stats.setSuscripcionActiva(suscripcionVigente);
        return stats;
    }
}
