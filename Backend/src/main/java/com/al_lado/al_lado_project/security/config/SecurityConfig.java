package com.al_lado.al_lado_project.security.config;

import com.al_lado.al_lado_project.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita las anotaciones como @PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Sin sesión no hay login al que redirigir: anónimos denegados reciben 401
                // (los autenticados sin rol reciben 403 vía AccessDeniedHandler por defecto).
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        // 1. Peticiones de Preflight (CORS) permitidas siempre
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 1b. Documentación OpenAPI/Swagger pública (solo describe la API)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 2. Endpoints públicos de autenticación y registro
                        // /auth/logout exige sesión: matcher previo para que no caiga
                        // en el permitAll de /auth/** (anónimos -> 401 limpio).
                        .requestMatchers(HttpMethod.POST, "/auth/logout", "/auth/password", "/auth/password/verify").authenticated()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/trabajadores", "/empleadores").permitAll()

                        // 3. Endpoints públicos de lectura (catálogos/foro)
                        .requestMatchers(HttpMethod.GET, "/oportunidades/**", "/comunicados/**", "/publicidades/**", "/foro/**").permitAll()

                        // 4. Panel de administración REST (protegido para ROLE_ADMIN)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/administradores/**").hasRole("ADMIN")
                        .requestMatchers("/reportes/*/resolver").hasRole("ADMIN")

                        // 5. Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                // ✅ Correcto
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}