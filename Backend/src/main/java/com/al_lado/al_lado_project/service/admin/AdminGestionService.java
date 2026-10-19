package com.al_lado.al_lado_project.service.admin;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.dto.trabajo.PostulacionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import com.al_lado.al_lado_project.mappers.comunidad.ComunicadoMapper;
import com.al_lado.al_lado_project.mappers.trabajo.PostulacionMapper;
import com.al_lado.al_lado_project.repository.comunidad.ComunicadoRepository;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.service.trabajo.OportunidadLaboralService;
import com.al_lado.al_lado_project.dto.comunidad.ReporteDTO;
import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;
import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Reporte;
import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import com.al_lado.al_lado_project.mappers.comunidad.ReporteMapper;
import com.al_lado.al_lado_project.mappers.comunidad.PublicacionForoMapper;
import com.al_lado.al_lado_project.mappers.comunidad.ValoracionMapper;
import com.al_lado.al_lado_project.repository.comunidad.ReporteRepository;
import com.al_lado.al_lado_project.repository.comunidad.PublicacionForoRepository;
import com.al_lado.al_lado_project.repository.comunidad.ValoracionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.JoinType;
import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;
import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Donacion;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.repository.comunidad.DonacionRepository;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import com.al_lado.al_lado_project.mappers.comunidad.DonacionMapper;
import com.al_lado.al_lado_project.mappers.usuario.SuscripcionMapper;
import com.al_lado.al_lado_project.dto.comunidad.PublicidadDTO;
import com.al_lado.al_lado_project.entity.comunidad.Publicidad;
import com.al_lado.al_lado_project.repository.comunidad.PublicidadRepository;
import com.al_lado.al_lado_project.mappers.comunidad.PublicidadMapper;
import com.al_lado.al_lado_project.mappers.comunidad.PerfilEmpresarialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consultas paginadas y filtros del panel administrativo.
 *
 * <p>El texto puede coincidir en cualquiera de los campos buscados y se combina con los demás
 * filtros. Los JOIN de estos listados no duplican un registro por coincidir en varios
 * campos.</p>
 *
 * <p>El controller valida estados y límites de página. Las escrituras se realizan en los
 * servicios de cada dominio.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminGestionService {
    private final ComunicadoRepository comunicadoRepository;
    private final ComunicadoMapper comunicadoMapper;
    private final PostulacionRepository postulacionRepository;
    private final PostulacionMapper postulacionMapper;
    private final OportunidadLaboralService oportunidades;
    private final ReporteRepository reportes;
    private final ReporteMapper reporteMapper;
    private final PublicacionForoRepository foro;
    private final PublicacionForoMapper foroMapper;
    private final ValoracionRepository valoraciones;
    private final ValoracionMapper valoracionMapper;
    private final TrabajadorRepository trabajadores;
    private final DonacionRepository donaciones;
    private final DonacionMapper donacionMapper;
    private final SuscripcionRepository suscripciones;
    private final SuscripcionMapper suscripcionMapper;

    private final PublicidadRepository publicidades;
    private final PublicidadMapper publicidadMapper;
    private final com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository empresas;
    private final PerfilEmpresarialMapper empresaMapper;

    /**
     * Publicidades con filtro de texto (título, nombre o email del anunciante) y
     * estado: {@code VIGENTES} (activa y hoy dentro del rango, inclusivo),
     * {@code INACTIVAS} (desactivada), {@code VENCIDAS} ({@code fechaFin} anterior
     * a hoy) o {@code PROGRAMADAS} (activa y {@code fechaInicio} futura).
     */
    public Page<PublicidadDTO> publicidades(String filtro, String estado, Pageable pageable) {
        Specification<Publicidad> spec = (root, query, cb) -> {
            var condition = cb.conjunction();
            var hoy = java.time.LocalDate.now();
            if ("VIGENTES".equals(estado)) condition = cb.and(condition, cb.isTrue(root.get("activa")), cb.lessThanOrEqualTo(root.get("fechaInicio"), hoy), cb.greaterThanOrEqualTo(root.get("fechaFin"), hoy));
            if ("INACTIVAS".equals(estado)) condition = cb.and(condition, cb.isFalse(root.get("activa")));
            if ("VENCIDAS".equals(estado)) condition = cb.and(condition, cb.lessThan(root.get("fechaFin"), hoy));
            if ("PROGRAMADAS".equals(estado)) condition = cb.and(condition, cb.isTrue(root.get("activa")), cb.greaterThan(root.get("fechaInicio"), hoy));
            if (filtro != null && !filtro.isBlank()) {
                String pattern = "%" + filtro.trim().toLowerCase(java.util.Locale.ROOT) + "%";
                var owner = root.join("anunciante", JoinType.LEFT);
                condition = cb.and(condition, cb.or(cb.like(cb.lower(root.get("titulo")), pattern), cb.like(cb.lower(owner.get("nombre")), pattern), cb.like(cb.lower(owner.get("email")), pattern)));
            }
            return condition;
        };
        return publicidades.findAll(spec, pageable).map(publicidadMapper::toDTO);
    }

    /**
     * Empleadores con su perfil (o sin él) buscando por nombre o CUIT del perfil o
     * nombre/email del empleador. Estado: {@code SIN_PERFIL} (empleadores que no
     * llegaron a crear perfil, que se conservan en la lista), {@code PENDIENTES}
     * (perfil sin verificar) o {@code VERIFICADAS}.
     */
    public Page<com.al_lado.al_lado_project.dto.admin.EmpresaAdminDTO> empresas(String filtro, String estado, Pageable pageable) {
        Specification<com.al_lado.al_lado_project.entity.usuario.Empleador> spec = (root, query, cb) -> {
            var perfil = root.join("perfilEmpresarial", JoinType.LEFT);
            var condition = cb.conjunction();
            if ("SIN_PERFIL".equals(estado)) condition = cb.and(condition, cb.isNull(perfil.get("id")));
            if ("PENDIENTES".equals(estado)) condition = cb.and(condition, cb.isFalse(perfil.get("verificado")));
            if ("VERIFICADAS".equals(estado)) condition = cb.and(condition, cb.isTrue(perfil.get("verificado")));
            if (filtro != null && !filtro.isBlank()) {
                String pattern = "%" + filtro.trim().toLowerCase(java.util.Locale.ROOT) + "%";
                condition = cb.and(condition, cb.or(cb.like(cb.lower(perfil.get("nombreEmpresa")), pattern), cb.like(cb.lower(perfil.get("cuit")), pattern), cb.like(cb.lower(root.get("nombre")), pattern), cb.like(cb.lower(root.get("email")), pattern)));
            }
            return condition;
        };
        return empresas.findAll(spec, pageable).map(owner -> {
            var resumen = new com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO();
            resumen.setId(owner.getId()); resumen.setNombre(owner.getNombre()); resumen.setEmail(owner.getEmail());
            return new com.al_lado.al_lado_project.dto.admin.EmpresaAdminDTO(owner.getId(), resumen, empresaMapper.toDTO(owner.getPerfilEmpresarial()));
        });
    }

    public Page<ComunicadoDTO> comunicados(String filtro, String alcance, Pageable pageable) {
        Specification<Comunicado> spec = (root, query, cb) -> {
            var condition = cb.conjunction();
            if (filtro != null && !filtro.isBlank()) {
                String pattern = "%" + filtro.trim().toLowerCase(java.util.Locale.ROOT) + "%";
                condition = cb.and(condition, cb.or(cb.like(cb.lower(root.get("titulo")), pattern), cb.like(cb.lower(root.get("contenido")), pattern)));
            }
            if (alcance != null && !alcance.isBlank()) {
                condition = cb.and(condition, cb.equal(cb.lower(root.get("alcance")), alcance.trim().toLowerCase(java.util.Locale.ROOT)));
            }
            return condition;
        };
        return comunicadoRepository.findAll(spec, pageable).map(comunicadoMapper::toDTO);
    }

    /**
     * Postulaciones de una oferta. Si la oferta no existe responde <b>404</b>
     * (valida contra {@code OportunidadLaboralService}) en lugar de devolver una
     * página vacía.
     */
    public Page<PostulacionDTO> postulaciones(Long oportunidadId, Pageable pageable) {
        oportunidades.obtenerPorId(oportunidadId); // Un ID inexistente debe responder 404, no una lista vacía.
        return postulacionRepository.findByOportunidadLaboralId(oportunidadId, pageable).map(postulacionMapper::toDTO);
    }

    /**
     * Reportes con texto libre (motivo, descripción o nombre de reportante/reportado)
     * y estado. Solo {@code TODOS} desactiva el filtro de estado; cualquier otro
     * valor, incluido {@code null}, se compara literalmente (el controller usa
     * {@code PENDIENTE} por defecto).
     */
    public Page<ReporteDTO> reportes(String filtro, String estado, Pageable pageable) {
        Specification<Reporte> spec = (root, query, cb) -> {
            var condition = cb.conjunction();
            if (!"TODOS".equals(estado)) condition = cb.and(condition, cb.equal(root.get("estado"), estado));
            if (filtro != null && !filtro.isBlank()) {
                String pattern = "%" + filtro.trim().toLowerCase(java.util.Locale.ROOT) + "%";
                condition = cb.and(condition, cb.or(
                    cb.like(cb.lower(root.get("motivo")), pattern),
                    cb.like(cb.lower(root.get("descripcion")), pattern),
                    cb.like(cb.lower(root.join("reportante", JoinType.LEFT).get("nombre")), pattern),
                    cb.like(cb.lower(root.join("reportado", JoinType.LEFT).get("nombre")), pattern)));
            }
            return condition;
        };
        return reportes.findAll(spec, pageable).map(reporteMapper::toDTO);
    }

    public Page<PublicacionForoDTO> foro(String filtro, Pageable pageable) {
        Specification<PublicacionForo> spec = (root, query, cb) -> {
            if (filtro == null || filtro.isBlank()) return cb.conjunction();
            String pattern = "%" + filtro.trim().toLowerCase(java.util.Locale.ROOT) + "%";
            return cb.or(cb.like(cb.lower(root.get("titulo")), pattern),
                cb.like(cb.lower(root.get("contenido")), pattern),
                cb.like(cb.lower(root.join("autor", JoinType.LEFT).get("nombre")), pattern));
        };
        return foro.findAll(spec, pageable).map(foroMapper::toDTO);
    }

    public Page<ValoracionDTO> valoraciones(Long trabajadorId, Pageable pageable) {
        if (!trabajadores.existsById(trabajadorId)) throw new ResourceNotFoundException("Trabajador no encontrado.");
        return valoraciones.findByTrabajadorId(trabajadorId, pageable).map(valoracionMapper::toDTO);
    }

    /**
     * Donaciones con texto libre (nombre/email del donante, mecanismo o asunto) y
     * estado. El literal {@code TODAS} desactiva el filtro; cualquier otro valor,
     * incluido {@code null}, se compara literalmente contra la columna y devuelve
     * página vacía si no coincide (el controller siempre envía un valor por defecto).
     */
    public Page<DonacionDTO> donaciones(String filtro, String estado, Pageable pageable) {
        Specification<Donacion> spec = (root, query, cb) -> {
            var condition = cb.conjunction();
            if (!"TODAS".equals(estado)) condition = cb.and(condition, cb.equal(root.get("estado"), estado));
            if (filtro != null && !filtro.isBlank()) {
                String pattern = "%" + filtro.trim().toLowerCase(java.util.Locale.ROOT) + "%";
                var usuario = root.join("usuario", JoinType.LEFT);
                condition = cb.and(condition, cb.or(cb.like(cb.lower(usuario.get("nombre")), pattern),
                    cb.like(cb.lower(usuario.get("email")), pattern),
                    cb.like(cb.lower(root.get("mecanismoPago")), pattern),
                    cb.like(cb.lower(root.get("asuntoTransferencia")), pattern)));
            }
            return condition;
        };
        return donaciones.findAll(spec, pageable).map(donacionMapper::toDTO);
    }

    /**
     * Suscripciones por usuario con texto libre sobre nombre/email. Estado
     * {@code ACTIVAS} / {@code INACTIVAS} usando el mismo criterio de vigencia que
     * {@code SuscripcionService.esProVigente}: {@code activa} y {@code fechaFin}
     * nula o mayor o igual a hoy (<b>inclusivo</b>). No filtra por nivel PRO.
     */
    public Page<SuscripcionDTO> suscripciones(String filtro, String estado, Pageable pageable) {
        Specification<Suscripcion> spec = (root, query, cb) -> {
            var condition = cb.conjunction();
            // Mismo criterio de vigencia que SuscripcionService.esProVigente.
            var vigente = cb.and(cb.isTrue(root.get("activa")), cb.or(cb.isNull(root.get("fechaFin")),
                cb.greaterThanOrEqualTo(root.get("fechaFin"), java.time.LocalDate.now())));
            if ("ACTIVAS".equals(estado)) condition = cb.and(condition, vigente);
            if ("INACTIVAS".equals(estado)) condition = cb.and(condition, cb.not(vigente));
            if (filtro != null && !filtro.isBlank()) {
                String pattern = "%" + filtro.trim().toLowerCase(java.util.Locale.ROOT) + "%";
                var usuario = root.join("usuario", JoinType.LEFT);
                condition = cb.and(condition, cb.or(cb.like(cb.lower(usuario.get("nombre")), pattern), cb.like(cb.lower(usuario.get("email")), pattern)));
            }
            return condition;
        };
        return suscripciones.findAll(spec, pageable).map(suscripcionMapper::toDTO);
    }
}
