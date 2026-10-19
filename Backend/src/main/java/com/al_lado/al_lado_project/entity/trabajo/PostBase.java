package com.al_lado.al_lado_project.entity.trabajo;

import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Base polimórfica de las publicaciones de trabajo, con <b>herencia
 * {@code JOINED}</b>: los datos comunes viven en {@code posts_base} y cada subtipo
 * aporta su tabla ({@code oportunidades_laborales}, {@code postulaciones}).
 *
 * <p>Es abstracta y se serializa/deserializa de forma polimórfica con la propiedad
 * JSON {@code type} ({@code oportunidad} / {@code postulacion}); a diferencia de
 * {@link com.al_lado.al_lado_project.entity.usuario.Usuario} <b>no tiene
 * {@code defaultImpl}</b>, por lo que un valor desconocido de {@code type} hace
 * fallar la deserialización.</p>
 *
 * <p>{@code fechaCreacion} la fija la aplicación en {@link #onCreate()} con el reloj
 * de la JVM (no un DEFAULT de base de datos) y solo si aún es nula; la columna está
 * marcada {@code updatable = false}, por lo que <b>nunca se modifica</b> después del
 * alta.</p>
 *
 * <p>{@code autor} es {@code LAZY} y de tipo {@link Usuario}: acceder al subtipo
 * concreto desde un proxy sin inicializar no es confiable (ver
 * {@link Postulacion#getTrabajador()}).</p>
 *
 * <p>Cuidado con {@link #equals(Object)}: exige {@code getClass()} idéntico y
 * {@code id != null}. Un <b>proxy</b> de Hibernate tiene una clase generada
 * distinta y una entidad sin persistir no tiene ID, así que comparar por igualdad
 * no es fiable aquí; conviene comparar los ID.</p>
 */
@Entity
@Table(name = "posts_base")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OportunidadLaboral.class, name = "oportunidad"),
        @JsonSubTypes.Type(value = Postulacion.class, name = "postulacion")
})
public abstract class PostBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    /**
     * Callback de {@code @PrePersist}: completa {@code fechaCreacion} con {@code
     * LocalDateTime.now()}
     * (hora de la aplicación) solo si quien llama al método no la envió. Corre una única vez, en
     * el alta; el {@code updatable = false} de la columna evita cambios posteriores.
     */
    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }

    public PostBase(String titulo, String contenido, Usuario autor) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.autor = autor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostBase postBase = (PostBase) o;
        return id != null && Objects.equals(id, postBase.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}