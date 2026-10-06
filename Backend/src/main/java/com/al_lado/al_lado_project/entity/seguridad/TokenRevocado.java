package com.al_lado.al_lado_project.entity.seguridad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Blacklist de JWT revocados (logout). Se guarda el hash del jti, nunca el
 * token. Las filas vencidas se purgan al registrar una revocación.
 */
@Entity
@Table(name = "tokens_revocados")
@Getter
@Setter
@NoArgsConstructor
public class TokenRevocado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String jti;

    @Column(length = 150)
    private String sujeto;

    @Column(nullable = false)
    private LocalDateTime expira;

    public TokenRevocado(String jti, String sujeto, LocalDateTime expira) {
        this.jti = jti;
        this.sujeto = sujeto;
        this.expira = expira;
    }
}
