package com.al_lado.al_lado_project.service.usuario;

import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;

/** Política compartida de vigencia para suscripciones y filtros administrativos. */
public final class SuscripcionVigencia {

    private SuscripcionVigencia() {}

    /** Activa y dentro del período inclusivo; una fecha final nula no vence. */
    public static boolean esVigente(Suscripcion suscripcion, LocalDate hoy) {
        return suscripcion.isActiva()
                && suscripcion.getFechaInicio() != null
                && !suscripcion.getFechaInicio().isAfter(hoy)
                && (suscripcion.getFechaFin() == null || !suscripcion.getFechaFin().isBefore(hoy));
    }

    /** Predicado SQL equivalente para listados paginados de suscripciones. */
    public static Predicate predicadoVigente(Root<Suscripcion> root, CriteriaBuilder cb, LocalDate hoy) {
        return cb.and(cb.isTrue(root.get("activa")),
                cb.lessThanOrEqualTo(root.get("fechaInicio"), hoy),
                cb.or(cb.isNull(root.get("fechaFin")), cb.greaterThanOrEqualTo(root.get("fechaFin"), hoy)));
    }
}
