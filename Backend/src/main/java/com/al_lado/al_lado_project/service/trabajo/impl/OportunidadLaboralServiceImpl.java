package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.FiltrosOportunidadesDTO;
import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.entity.trabajo.Etiqueta;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.trabajo.EtiquetaRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.trabajo.OportunidadLaboralService;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.mappers.trabajo.OportunidadLaboralMapper;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OportunidadLaboralServiceImpl implements OportunidadLaboralService {

    private final OportunidadLaboralRepository repository;
    private final EmpleadorRepository empleadorRepository;
    private final EtiquetaRepository etiquetaRepository;
    private final OportunidadLaboralMapper mapper;
    private final TrabajadorRepository trabajadorRepository;
    private final SuscripcionService suscripcionService;

    @Override
    @Transactional(readOnly = true)
    public List<OportunidadLaboralDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OportunidadLaboralDTO> listarOportunidades(String filtro, Pageable pageable) {
        return repository.findAll(specBusqueda(filtro, null, null), pageable).map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OportunidadLaboralDTO> listarOportunidades(String filtro, String modalidad, List<String> etiquetas, Pageable pageable) {
        return repository.findAll(specBusqueda(filtro, modalidad, etiquetas), pageable).map(mapper::toDTO);
    }

    /**
     * RF-02: búsqueda combinada de oportunidades. Todos los criterios son opcionales
     * y se acumulan con AND; dentro del texto libre van unidos con OR.
     *
     * @param filtro    texto libre: se compara contra título, contenido, ubicación y
     *                  nombre de etiqueta
     * @param modalidad modalidad exacta sin distinguir mayúsculas
     *                  (REMOTO / PRESENCIAL / HIBRIDO)
     * @param etiquetas la oportunidad debe tener al menos una de estas etiquetas
     * @return sólo oportunidades <b>activas</b>
     */
    @Override
    @Transactional(readOnly = true)
    public List<OportunidadLaboralDTO> buscarPorFiltros(String filtro, String modalidad, List<String> etiquetas) {
        return repository.findAll(specBusqueda(filtro, modalidad, etiquetas)).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Specification compartida por listarOportunidades() y buscarPorFiltros(), para que
     * ambos caminos definan "activa" y el texto libre exactamente igual.
     *
     * Se hace un único LEFT JOIN con "etiquetas" cuando hace falta, porque tanto el
     * texto libre como la selección de etiquetas usan la misma asociación. El join
     * puede repetir la misma oportunidad varias veces, por eso se fuerza distinct().
     */
    private Specification<OportunidadLaboral> specBusqueda(String filtro, String modalidad, List<String> etiquetas) {
        return specBusqueda(filtro, modalidad, etiquetas, false);
    }

    private Specification<OportunidadLaboral> specBusqueda(String filtro, String modalidad, List<String> etiquetas, boolean soloDestacadas) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Sólo oportunidades activas
            predicates.add(criteriaBuilder.isTrue(root.get("activa")));

            // HU empleador "Oportunidad laboral pro": filtrar solo destacadas
            if (soloDestacadas) {
                predicates.add(criteriaBuilder.isTrue(root.get("destacada")));
            }

            boolean hayFiltro = filtro != null && !filtro.trim().isEmpty();
            List<String> nombresEtiqueta = normalizarEtiquetas(etiquetas);
            boolean hayEtiquetas = !nombresEtiqueta.isEmpty();

            // El mismo join cubre las dos condiciones que necesitan "etiquetas"
            Join<Object, Object> etiquetasJoin = (hayFiltro || hayEtiquetas)
                    ? root.join("etiquetas", JoinType.LEFT)
                    : null;

            // Texto libre: título, contenido, ubicación o nombre de etiqueta
            if (hayFiltro) {
                String patron = "%" + filtro.trim().toLowerCase() + "%";
                Predicate coincideTitulo = criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), patron);
                Predicate coincideContenido = criteriaBuilder.like(criteriaBuilder.lower(root.get("contenido")), patron);
                Predicate coincideUbicacion = criteriaBuilder.like(criteriaBuilder.lower(root.get("ubicacion")), patron);
                Predicate coincideEtiqueta = criteriaBuilder.like(criteriaBuilder.lower(etiquetasJoin.get("nombre")), patron);

                predicates.add(criteriaBuilder.or(coincideTitulo, coincideContenido, coincideUbicacion, coincideEtiqueta));
            }

            // Modalidad exacta
            if (modalidad != null && !modalidad.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("modalidad")),
                        modalidad.trim().toLowerCase()));
            }

            // Al menos una de las etiquetas pedidas (en minúsculas: lower() del lado izquierdo)
            if (hayEtiquetas) {
                Expression<String> nombreEtiqueta = criteriaBuilder.lower(etiquetasJoin.get("nombre"));
                predicates.add(nombreEtiqueta.in(nombresEtiqueta));
            }

            // El join sobre "etiquetas" puede duplicar filas de la misma oportunidad
            if (hayFiltro || hayEtiquetas) {
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Limpia la lista de etiquetas pedidas: quita nulos, espacios y vacíos, pasa a
     * minúsculas y elimina repetidos. Devuelve lista inmutable si la entrada es null.
     */
    private List<String> normalizarEtiquetas(List<String> etiquetas) {
        if (etiquetas == null) {
            return List.of();
        }
        return etiquetas.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OportunidadLaboralDTO obtenerPorId(Long id) {
        OportunidadLaboral oportunidad = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidad no encontrada con ID: " + id));
        return mapper.toDTO(oportunidad);
    }

    /**
     * Mecanismo: la transacción arranca bloqueando el <b>empleador</b>
     * ({@code findByIdForUpdate}), no la oferta. Ese lock serializa las altas
     * concurrentes del mismo propietario, de modo que la comprobación de cupo
     * ({@code puedePublicarOferta}) y el {@code save} posterior no se intercalen
     * entre transacciones. Las ofertas de empleadores distintos no comparten lock.
     *
     * <p>Al editar se recarga la entidad existente y se actualizan sus campos: se trabaja
     * sobre una entidad gestionada, por lo que no se crea un registro nuevo ni se
     * pierden los campos no enviados en el DTO (salvo los que sí se sobrescriben).</p>
     */
    @Override
    @Transactional
    public OportunidadLaboralDTO guardar(OportunidadLaboralDTO dto) {
        if (dto.getAutor() == null || dto.getAutor().getId() == null) {
            throw new BusinessRuleException("El autor (Empleador) es obligatorio para crear la oportunidad.");
        }

        Long autorId = dto.getAutor().getId();
        // Carga con bloqueo pesimista: serializa peticiones concurrentes del
        // mismo empleador para que el control de limite (puedePublicarOferta)
        // y el save posterior no sufran check-then-act entre transacciones.
        Empleador empleador = empleadorRepository.findByIdForUpdate(autorId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleador no encontrado con ID: " + autorId));

        OportunidadLaboral oportunidad = dto.getId() != null ?
                repository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Oportunidad no encontrada con ID: " + dto.getId()))
                : mapper.toEntity(dto);

        boolean esNueva = (dto.getId() == null);

        // Titularidad ANTES de mutar: crear exige ser el propio empleador;
        // actualizar exige ser el dueño original (o admin) e impide transferir
        // la oferta cambiando el autor desde el DTO.
        if (esNueva) {
            SecurityUtils.requireSelfOrAdmin(autorId);
        } else {
            Long propietarioId = oportunidad.getAutor() != null ? oportunidad.getAutor().getId() : null;
            SecurityUtils.requireSelfOrAdmin(propietarioId);
            if (!autorId.equals(propietarioId) && !SecurityUtils.isAdmin()) {
                throw new BusinessRuleException("No puede transferir una oferta a otro empleador.");
            }
        }

        oportunidad.setTitulo(dto.getTitulo());
        oportunidad.setContenido(dto.getContenido());
        oportunidad.setUbicacion(dto.getUbicacion());
        oportunidad.setModalidad(dto.getModalidad());
        oportunidad.setRangoSalarial(dto.getRangoSalarial());
        oportunidad.setAutor(empleador);

        // Las etiquetas se resuelven contra el repositorio para trabajar
        // siempre con entidades gestionadas (evita duplicados por unique
        // y re-inserciones en lote al reasignar la coleccion completa).
        if (dto.getEtiquetas() != null) {
            Set<Etiqueta> etiquetas = dto.getEtiquetas().stream()
                    .map(e -> resolverEtiqueta(e.getId(), e.getNombre()))
                    .collect(Collectors.toSet());
            oportunidad.setEtiquetas(etiquetas);
        }

        if (esNueva) {
            oportunidad.setActiva(true);

            // RF-04, HU-01: Verificar límite de ofertas del plan Básico (3 ofertas/mes)
            if (!suscripcionService.puedePublicarOferta(autorId)) {
                throw new BusinessRuleException("Límite mensual de ofertas alcanzado. " +
                    "Actualiza a Nivel Pro para publicar ofertas ilimitadas.");
            }
        } else {
            oportunidad.setActiva(dto.isActiva());
        }

        // HU empleador "Oportunidad laboral pro": destacar exige plan PRO vigente.
        if (dto.isDestacada() && !suscripcionService.esProVigente(autorId)) {
            throw new BusinessRuleException("Destacar una oferta requiere una suscripción PRO vigente.");
        }
        oportunidad.setDestacada(dto.isDestacada());

        // Se persiste la entidad con su ID asignado.
        OportunidadLaboral guardada = repository.save(oportunidad);

        return mapper.toDTO(guardada);
    }

    private Etiqueta resolverEtiqueta(Long id, String nombre) {
        if (id != null) {
            return etiquetaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + id));
        }
        String nombreNormalizado = nombre != null ? nombre.trim() : null;
        if (nombreNormalizado == null || nombreNormalizado.isEmpty()) {
            throw new BusinessRuleException("El nombre de la etiqueta es obligatorio.");
        }
        return etiquetaRepository.findByNombreIgnoreCase(nombreNormalizado)
                .orElseGet(() -> etiquetaRepository.save(new Etiqueta(nombreNormalizado)));
    }

    private List<Trabajador> encontrarTrabajadoresPorEtiqueta(String nombreEtiqueta) {
        // Consulta delegada a la BD (JOIN indexado + FETCH de preferencia):
        // evita cargar toda la tabla de trabajadores a memoria.
        return trabajadorRepository.findByEspecialidadNombreIgnoreCase(nombreEtiqueta);
    }

    @Override
    @Transactional
    public OportunidadLaboralDTO cerrarOportunidad(Long id) {
        OportunidadLaboral oportunidad = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidad no encontrada con ID: " + id));

        Long propietarioId = oportunidad.getAutor() != null ? oportunidad.getAutor().getId() : null;
        SecurityUtils.requireSelfOrAdmin(propietarioId);

        oportunidad.setActiva(false);
        return mapper.toDTO(repository.save(oportunidad));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        OportunidadLaboral oportunidad = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidad no encontrada con ID: " + id));
        Long propietarioId = oportunidad.getAutor() != null ? oportunidad.getAutor().getId() : null;
        SecurityUtils.requireSelfOrAdmin(propietarioId);
        try {
            repository.delete(oportunidad);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new BusinessRuleException("No se puede eliminar la oferta porque tiene postulaciones asociadas.");
        }
    }

    @Override
    public List<Trabajador> obtenerTrabajadoresPorEtiqueta(String nombreEtiqueta) {
        return encontrarTrabajadoresPorEtiqueta(nombreEtiqueta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OportunidadLaboralDTO> listarConFiltros(FiltrosOportunidadesDTO filtros) {
        String filtro = filtros != null ? filtros.getFiltro() : null;
        String modalidad = filtros != null ? filtros.getModalidad() : null;
        List<String> etiquetas = filtros != null && filtros.getEtiquetas() != null
                ? filtros.getEtiquetas() : null;
        boolean soloDestacadas = filtros != null && Boolean.TRUE.equals(filtros.getSoloDestacadas());

        return repository.findAll(specBusqueda(filtro, modalidad, etiquetas, soloDestacadas)).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OportunidadLaboralDTO> propuestasProParaTrabajador(Long trabajadorId) {
        SecurityUtils.requireSelfOrAdmin(trabajadorId);
        if (!suscripcionService.esProVigente(trabajadorId)) {
            throw new BusinessRuleException("Acceder a propuestas PRO requiere una suscripción PRO vigente.");
        }
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + trabajadorId));

        List<String> especialidades = trabajador.getEspecialidades() == null ? List.of()
                : trabajador.getEspecialidades().stream()
                        .map(e -> e.getNombre())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        if (especialidades.isEmpty()) {
            return List.of();
        }

        return buscarPorFiltros(null, null, especialidades).stream()
                .sorted(Comparator.comparing(OportunidadLaboralDTO::isDestacada).reversed())
                .collect(Collectors.toList());
    }
}