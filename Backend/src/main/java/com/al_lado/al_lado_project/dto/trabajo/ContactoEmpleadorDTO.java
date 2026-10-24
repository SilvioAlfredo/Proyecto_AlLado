package com.al_lado.al_lado_project.dto.trabajo;

import lombok.Data;

/**
 * Datos de contacto del empleador dueño de una oferta (AL-SEC-006).
 *
 * <p>El detalle público de la oferta no incluye estos datos: los entrega sólo
 * {@code GET /oportunidades/{id}/contacto}, al titular, a un administrador o a quien
 * se postuló a la oferta.</p>
 */
@Data
public class ContactoEmpleadorDTO {

    private String nombre;
    private String email;
    private String telefono;
}
