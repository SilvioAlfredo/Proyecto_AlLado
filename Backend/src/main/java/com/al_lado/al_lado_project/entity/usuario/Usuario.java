package com.al_lado.al_lado_project.entity.usuario;

import com.al_lado.al_lado_project.entity.comunidad.Donacion;
import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import com.al_lado.al_lado_project.entity.comunidad.Reporte;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = Empleador.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Empleador.class, name = "empleador"),
        @JsonSubTypes.Type(value = Trabajador.class, name = "trabajador"),
        @JsonSubTypes.Type(value = Administrador.class, name = "administrador")
})
public abstract class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Suscripcion suscripcion;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    private Set<PublicacionForo> publicacionesForo = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Set<Donacion> donaciones = new HashSet<>();

    @OneToMany(mappedBy = "reportante")
    private Set<Reporte> reportesEnviados = new HashSet<>();

    @OneToMany(mappedBy = "reportado")
    private Set<Reporte> reportesRecibidos = new HashSet<>();

    public Usuario(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    public void setSuscripcion(Suscripcion suscripcion) {
        if (suscripcion == null) {
            if (this.suscripcion != null) {
                this.suscripcion.setUsuario(null);
            }
        } else {
            suscripcion.setUsuario(this);
        }
        this.suscripcion = suscripcion;
    }

    // =======================================================
    // IMPLEMENTACIÓN DE USERDETAILS (SPRING SECURITY)
    // =======================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this instanceof Administrador) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (this instanceof Empleador) {
            return List.of(new SimpleGrantedAuthority("ROLE_EMPLEADOR"));
        } else if (this instanceof Trabajador) {
            return List.of(new SimpleGrantedAuthority("ROLE_TRABAJADOR"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}