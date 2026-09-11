package com.al_lado.al_lado_project.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED) // Mapea la herencia en tablas separadas
@Data
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type", // Crea una propiedad virtual "type" en el JSON
        defaultImpl = Empleador.class // Por si no se especifica, usa Empleador por defecto
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Empleador.class, name = "empleador"),
        @JsonSubTypes.Type(value = Trabajador.class, name = "trabajador")
})
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;
}