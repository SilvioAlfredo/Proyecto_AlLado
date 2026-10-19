package com.al_lado.al_lado_project.dto.admin;

import org.springframework.data.domain.Page;
import java.util.List;

/**
 * Respuesta paginada para los listados del panel de administración.
 *
 * <p>Lo usan los listados de {@code AdminGestionController}. Otros listados
 * paginados, como los de oportunidades y donaciones, devuelven únicamente
 * {@code Page.getContent()} y no incluyen estos metadatos.</p>
 *
 * @param content       elementos de la página actual
 * @param page          número de página <b>desde cero</b> (es {@code Page.getNumber()}),
 *                      no el índice que suele esperar el front
 * @param totalPages    cantidad total de páginas (0 si no hay resultados)
 * @param totalElements total de registros que cumplen el filtro, no el tamaño de esta página
 */
public record PaginaDTO<T>(List<T> content, int page, int totalPages, long totalElements) {

    /**
     * Convierte una {@link Page} de Spring Data preservando el contenido y los
     * totales del filtro aplicado.
     *
     * @param result página resultante de la consulta
     */
    public static <T> PaginaDTO<T> from(Page<T> result) {
        return new PaginaDTO<>(result.getContent(), result.getNumber(), result.getTotalPages(), result.getTotalElements());
    }
}
