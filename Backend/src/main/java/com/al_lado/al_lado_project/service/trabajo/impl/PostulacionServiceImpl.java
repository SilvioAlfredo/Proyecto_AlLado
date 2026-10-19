package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Notificacion;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.al_lado.al_lado_project.dto.usuario.EstadisticasTrabajadorDTO;
import java.math.BigDecimal;

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

    @Override
    @Transactional(readOnly = true)
    public List<PostulacionDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PostulacionDTO obtenerPorId(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulación no encontrada con ID: " + id));
        return mapper.toDTO(postulacion);
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

        // Contar valoraciones recibidas usando el repositorio de valoraciones
        Long valoracionesCount = valoracionRepository.countByTrabajadorId(trabajadorId);

        // Calcular promedio de valoraciones usando el query del repositorio
        Double promedioDouble = valoracionRepository.calcularPromedioPuntuacionPorTrabajadorId(trabajadorId);
        BigDecimal promedio = promedioDouble != null ? BigDecimal.valueOf(promedioDouble) : BigDecimal.ZERO;

        // Contar postulaciones por estado con el repository de postulaciones
        long completadas = repository.countByAutorIdAndEstadoIgnoreCase(
                trabajadorId, Postulacion.ESTADO_COMPLETADA);
        long seleccionadas = repository.countByAutorIdAndEstadoIgnoreCase(
                trabajadorId, Postulacion.ESTADO_SELECCIONADA);
        long enviadas = repository.countByAutorId(trabajadorId);

        // Estadísticasplaceholder para ingresos
        EstadisticasTrabajadorDTO stats = new EstadisticasTrabajadorDTO();
        stats.setId(trabajador.getId());
        stats.setNombre(trabajador.getNombre());
        stats.setEmail(trabajador.getEmail());
        stats.setIngresosTotales(BigDecimal.ZERO);
        stats.setIngresosUltimos3Meses(BigDecimal.ZERO);
        stats.setValoracionesCount(valoracionesCount != null ? valoracionesCount.intValue() : 0);
        stats.setPromedioValoraciones(promedio);
        stats.setOfertasPublicadas(0);
        stats.setPostulacionesEnviadas((int) enviadas);
        stats.setPostulacionesSeleccionadas((int) seleccionadas);
        stats.setPostulacionesCompletadas((int) completadas);
        stats.setNivelRecomendacion(4);
        stats.setPerfilCompleto(trabajador.getFotografia() != null && !trabajador.getFotografia().isEmpty());
        stats.setUltimoLogin(null);
        stats.setSuscripcionActiva(trabajador.getSuscripcion() != null && trabajador.getSuscripcion().isActiva());

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticasTrabajadorDTO obtenerEstadisticasTrabajadorPorEmail(String email) {
        // Buscar trabajador por email
        Trabajador trabajador = trabajadorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con email: " + email));
        SecurityUtils.requireSelfOrAdmin(trabajador.getId());

        // Contar valoraciones recibidas usando el repositorio de valoraciones
        Long valoracionesCount = valoracionRepository.countByTrabajadorId(trabajador.getId());

        // Calcular promedio de valoraciones usando el query del repositorio
        Double promedioDouble = valoracionRepository.calcularPromedioPuntuacionPorTrabajadorId(trabajador.getId());
        BigDecimal promedio = promedioDouble != null ? BigDecimal.valueOf(promedioDouble) : BigDecimal.ZERO;

        // Contar postulaciones por estado con el repository de postulaciones
        long completadas = repository.countByAutorIdAndEstadoIgnoreCase(
                trabajador.getId(), Postulacion.ESTADO_COMPLETADA);
        long seleccionadas = repository.countByAutorIdAndEstadoIgnoreCase(
                trabajador.getId(), Postulacion.ESTADO_SELECCIONADA);
        long enviadas = repository.countByAutorId(trabajador.getId());

        // Estadísticas placeholder para ingresos
        EstadisticasTrabajadorDTO stats = new EstadisticasTrabajadorDTO();
        stats.setId(trabajador.getId());
        stats.setNombre(trabajador.getNombre());
        stats.setEmail(trabajador.getEmail());
        stats.setIngresosTotales(BigDecimal.ZERO);
        stats.setIngresosUltimos3Meses(BigDecimal.ZERO);
        stats.setValoracionesCount(valoracionesCount != null ? valoracionesCount.intValue() : 0);
        stats.setPromedioValoraciones(promedio);
        stats.setOfertasPublicadas(0);
        stats.setPostulacionesEnviadas((int) enviadas);
        stats.setPostulacionesSeleccionadas((int) seleccionadas);
        stats.setPostulacionesCompletadas((int) completadas);
        stats.setNivelRecomendacion(4);
        stats.setPerfilCompleto(trabajador.getFotografia() != null && !trabajador.getFotografia().isEmpty());
        stats.setUltimoLogin(null);
        stats.setSuscripcionActiva(trabajador.getSuscripcion() != null && trabajador.getSuscripcion().isActiva());

        return stats;
    }
}