package com.al_lado.al_lado_project.entity.perfil;

import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "preferencias_notificacion")
@Getter
@Setter
@NoArgsConstructor
public class PreferenciaNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notificar_email", nullable = false)
    private boolean notificarEmail = true;

    @Column(name = "notificar_push", nullable = false)
    private boolean notificarPush = true;

    @Column(name = "alertas_nuevas_ofertas", nullable = false)
    private boolean alertasNuevasOfertas = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", nullable = false, unique = true)
    @JsonIgnore
    private Trabajador trabajador;

    public PreferenciaNotificacion(Trabajador trabajador, boolean notificarEmail, boolean notificarPush, boolean alertasNuevasOfertas) {
        this.trabajador = trabajador;
        this.notificarEmail = notificarEmail;
        this.notificarPush = notificarPush;
        this.alertasNuevasOfertas = alertasNuevasOfertas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreferenciaNotificacion that = (PreferenciaNotificacion) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}