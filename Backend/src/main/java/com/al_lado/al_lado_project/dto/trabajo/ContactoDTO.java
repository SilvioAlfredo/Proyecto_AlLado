package com.al_lado.al_lado_project.dto.trabajo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos de contacto del empleador de una oportunidad (AL-SEC-006).
 *
 * <p>Se exponen en un endpoint aparte porque el contacto es un dato de la relación
 * laboral: lo ven el titular de la oferta, los administradores y quien se postuló a
 * ella, no cualquier visitante del catálogo.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactoDTO {

    private String nombre;
    private String email;
    private String telefono;
}
