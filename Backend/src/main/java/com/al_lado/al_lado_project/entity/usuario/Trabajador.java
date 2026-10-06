package com.al_lado.al_lado_project.entity.usuario;

import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import com.al_lado.al_lado_project.entity.perfil.HorarioDisponibilidad;
import com.al_lado.al_lado_project.entity.perfil.PortafolioItem;
import com.al_lado.al_lado_project.entity.perfil.PreferenciaNotificacion;
import com.al_lado.al_lado_project.entity.trabajo.Etiqueta;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trabajadores")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter
@Setter
@NoArgsConstructor
public class Trabajador extends Usuario {

    @OneToMany(mappedBy = "autor")
    private Set<Postulacion> postulaciones = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "trabajador_etiqueta",
            joinColumns = @JoinColumn(name = "trabajador_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    private Set<Etiqueta> especialidades = new HashSet<>();

    @OneToMany(mappedBy = "trabajador", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PortafolioItem> portafolioItems = new HashSet<>();

    @OneToMany(mappedBy = "trabajador", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HorarioDisponibilidad> horarios = new HashSet<>();

    @OneToOne(mappedBy = "trabajador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PreferenciaNotificacion preferenciaNotificacion;

    @OneToMany(mappedBy = "trabajador")
    private Set<Valoracion> valoracionesRecibidas = new HashSet<>();

    @Column(length = 255)
    private String fotografia; // RF-02, HU-03: campo solicitado en la spec

    @Column(columnDefinition = "TEXT")
    private String biografia; // RF-02, HU-03: campo solicitado en la spec

    @Column(columnDefinition = "TEXT")
    private String certificados; // RF-02, HU-03: campo solicitado en la spec

    @Column(length = 100)
    private String sector; // sector barrial o zona (HU-13 CA-02, RF-13)

    @Column(length = 255)
    private String tokenPush; // Token para notificaciones push (FCM/APNs)

    public Trabajador(String nombre, String email, String password) {
        super(nombre, email, password);
    }

    public void setPreferenciaNotificacion(PreferenciaNotificacion preferenciaNotificacion) {
        if (preferenciaNotificacion == null) {
            if (this.preferenciaNotificacion != null) {
                this.preferenciaNotificacion.setTrabajador(null);
            }
        } else {
            preferenciaNotificacion.setTrabajador(this);
        }
        this.preferenciaNotificacion = preferenciaNotificacion;
    }

    // Métodos auxiliares M:N con Etiqueta
    public void addEspecialidad(Etiqueta etiqueta) {
        this.especialidades.add(etiqueta);
        etiqueta.getTrabajadores().add(this);
    }

    public void removeEspecialidad(Etiqueta etiqueta) {
        this.especialidades.remove(etiqueta);
        etiqueta.getTrabajadores().remove(this);
    }
}