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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Usuario raíz del modelo, con <b>herencia {@code JOINED}</b>: cada subtipo tiene su
 * propia tabla unida a {@code usuarios} por la clave primaria.
 *
 * <p><b>El rol no es un campo</b>: se deriva del subtipo con
 * {@code instanceof} en {@link #getAuthorities()} (ADMIN → {@code ROLE_ADMIN},
 * EMPLEADOR → {@code ROLE_EMPLEADOR}, TRABAJADOR → {@code ROLE_TRABAJADOR} y
 * {@code ROLE_USER} si no coincide ninguno). Un rol «cambiado» en la base solo
 * tendría efecto moviendo la fila a otra tabla de subtipo.</p>
 *
 * <p>Implementa {@link UserDetails} para ser el principal de Spring Security:</p>
 * <ul>
 *   <li>{@link #getUsername()} devuelve el email; la contraseña vive en un campo de
 *       solo escritura JSON ({@code WRITE_ONLY}), por lo que nunca sale en la
 *       respuesta.</li>
 *   <li>Solo {@link #isEnabled()} (flag {@code habilitado}) decide si la cuenta se
 *       autentica: caducidad de cuenta y de credenciales están en {@code true}
 *       fijo y {@code emailVerificado} <b>no</b> se exige para entrar.</li>
 * </ul>
 *
 * <p>Identidad: {@link #equals(Object)} exige {@code id != null}, así que dos
 * entidades <b>transitorias</b> (sin persistir) nunca son iguales; el
 * {@code hashCode} es constante por clase y estable en colecciones. A diferencia de
 * {@code PostBase}, usa {@code instanceof} y no {@code getClass()}, por lo que
 * tolera proxies de Hibernate.</p>
 *
 * <p>Relaciones: {@code suscripcion} es {@code LAZY} con sincronización bidireccional
 * en {@link #setSuscripcion(Suscripcion)}; foro y donaciones tienen
 * {@code CascadeType.ALL}, mientras que reportes (enviados/recibidos) no tienen
 * cascada: borrar un usuario no borra sus reportes.</p>
 */
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

    @Column(nullable = false, length = 20)
    private String telefono; // RF-01, HU-04: campo teléfono obligatorio con código de país

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

    /**
     * Mantiene coherente la relación bidireccional {@code Usuario ↔ Suscripcion}:
     * al asignar, enlaza también el lado inverso; al vaciar, desengancha la anterior.
     * Es el único punto de sincronización de esa asociación.
     *
     * @param suscripcion suscripción a vincular o {@code null} para desvincular
     */
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

    /**
     * Rol derivado del subtipo (no de una columna): un único {@code GrantedAuthority}
     * según {@code instanceof}. Si la entidad es una que no está contemplada, cae en
     * {@code ROLE_USER}, que ningún {@code @PreAuthorize} del sistema espera.
     */
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

    @Column(name = "habilitado", nullable = false)
    private boolean habilitado = true;

    @Column(name = "email_verificado", nullable = false)
    private boolean emailVerificado = false;

    // Token de recuperación de contraseña (AL-SEC-004).
    // Se guarda el jti del token emitido, nunca el token completo: con el jti
    // alcanza para invalidarlo, y una copia de la base no permite resets.
    @Column(name = "password_reset_token", length = 100)
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;

    // Token de verificación de correo (AL-SEC-004): mismo criterio que el de
    // recuperación (se guarda el jti) y se invalida al consumirse.
    @Column(name = "email_verification_token", length = 100)
    private String emailVerificationToken;

    @Column(name = "email_verification_token_expiry")
    private LocalDateTime emailVerificationTokenExpiry;

    /**
     * Invalida el token de recuperación: deja la cuenta sin token pendiente.
     *
     * <p>Se llama tras consumir el token, de modo que no pueda reutilizarse.</p>
     */
    public void clearPasswordResetToken() {
        this.passwordResetToken = null;
        this.passwordResetTokenExpiry = null;
    }

    /**
     * Invalida el token de verificación de correo: deja la cuenta sin token pendiente.
     *
     * <p>Se llama tras consumir el token, de modo que no pueda reutilizarse.</p>
     */
    public void clearEmailVerificationToken() {
        this.emailVerificationToken = null;
        this.emailVerificationTokenExpiry = null;
    }

    @Override
    public boolean isEnabled() {
        return this.habilitado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return id != null && Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Usuario.class.hashCode();
    }
}