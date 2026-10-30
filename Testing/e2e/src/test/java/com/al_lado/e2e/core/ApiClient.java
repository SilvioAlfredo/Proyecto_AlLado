package com.al_lado.e2e.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Cliente minimo de la API del backend.
 *
 * <p>Permite que un caso E2E compruebe el estado real del servidor y no solo lo
 * que la pantalla muestra: por ejemplo, localizar una oportunidad cerrada que el
 * listado del trabajador no muestra por filtrar solo las activas.</p>
 */
public final class ApiClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /** Inicia sesion en la API y devuelve el token JWT. */
    public String login(String email, String password) {
        JsonNode body = post("/auth/login", MAPPER.createObjectNode()
                .put("email", email)
                .put("password", password));
        return body.path("token").asText();
    }

    /** Ejecuta un GET autenticado y devuelve el cuerpo JSON. */
    public JsonNode get(String path, String token) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(E2EConfig.apiUrl() + path))
                .header("Authorization", "Bearer " + token)
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();
        return send(request);
    }

    /** Ejecuta un POST autenticado con cuerpo JSON. */
    public JsonNode post(String path, JsonNode payload, String token) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(E2EConfig.apiUrl() + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(20))
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()));
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return send(builder.build());
    }

    /** Ejecuta un POST autenticado con cuerpo vacio. */
    public JsonNode post(String path, String token) {
        return post(path, MAPPER.createObjectNode(), token);
    }

    /** Ejecuta un PUT autenticado con cuerpo JSON. */
    public JsonNode put(String path, JsonNode payload, String token) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(E2EConfig.apiUrl() + path))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .timeout(Duration.ofSeconds(20))
                .PUT(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();
        return send(request);
    }

    /** Primera oportunidad cerrada que devuelve la API. */
    public Optional<Long> firstClosedOpportunityId(String token) {
        JsonNode oportunidades = get("/oportunidades", token);
        for (JsonNode oportunidad : oportunidades) {
            if (!oportunidad.path("activa").asBoolean(true)) {
                return Optional.of(oportunidad.path("id").asLong());
            }
        }
        return Optional.empty();
    }

    /** Titulos de las oportunidades activas que devuelve la API. */
    public List<String> activeOpportunityTitles(String token) {
        return activeOpportunityTitles(token, "/oportunidades/filtrar");
    }

    /**
     * Titulos de las oportunidades activas segun un endpoint concreto.
     *
     * <p>La pantalla de oportunidades consume {@code POST /oportunidades/filtrar},
     * que devuelve la lista completa; {@code GET /oportunidades} esta paginado
     * (10 por pagina) y devuelve un subconjunto. Para comparar contra la UI hay
     * que usar el mismo origen de datos.</p>
     */
    public List<String> activeOpportunityTitles(String token, String endpoint) {
        List<String> titles = new ArrayList<>();
        JsonNode oportunidades = endpoint.startsWith("/oportunidades/filtrar")
                ? post(endpoint, token)
                : get(endpoint, token);
        for (JsonNode oportunidad : oportunidades) {
            if (oportunidad.path("activa").asBoolean(true)) {
                titles.add(oportunidad.path("titulo").asText());
            }
        }
        return titles;
    }

    /** Titulos de las oportunidades activas destacadas. */
    public List<String> highlightedOpportunityTitles(String token) {
        List<String> titles = new ArrayList<>();
        for (JsonNode oportunidad : post("/oportunidades/filtrar", token)) {
            if (oportunidad.path("activa").asBoolean(true)
                    && oportunidad.path("destacada").asBoolean(false)) {
                titles.add(oportunidad.path("titulo").asText());
            }
        }
        return titles;
    }

    /** Titulos de las oportunidades activas de una modalidad dada. */
    public List<String> opportunityTitlesByModality(String token, String modalidad) {
        List<String> titles = new ArrayList<>();
        for (JsonNode oportunidad : post("/oportunidades/filtrar", token)) {
            if (oportunidad.path("activa").asBoolean(true)
                    && modalidad.equalsIgnoreCase(oportunidad.path("modalidad").asText())) {
                titles.add(oportunidad.path("titulo").asText());
            }
        }
        return titles;
    }

    /** Titulos de las oportunidades activas de una ubicacion dada. */
    public List<String> opportunityTitlesByLocation(String token, String ubicacion) {
        List<String> titles = new ArrayList<>();
        for (JsonNode oportunidad : post("/oportunidades/filtrar", token)) {
            if (oportunidad.path("activa").asBoolean(true)
                    && oportunidad.path("ubicacion").asText().toLowerCase()
                            .contains(ubicacion.toLowerCase())) {
                titles.add(oportunidad.path("titulo").asText());
            }
        }
        return titles;
    }

    /** Titulos de las publicaciones del foro. */
    public List<String> forumTitles(String token) {
        List<String> titles = new ArrayList<>();
        JsonNode publicaciones = get("/foro", token);
        for (JsonNode publicacion : publicaciones) {
            titles.add(publicacion.path("titulo").asText());
        }
        return titles;
    }

    /** Primera modalidad presente entre las oportunidades activas. */
    public String firstModality(String token) {
        for (JsonNode oportunidad : post("/oportunidades/filtrar", token)) {
            String modalidad = oportunidad.path("modalidad").asText();
            if (!modalidad.isBlank()) {
                return modalidad;
            }
        }
        throw new IllegalStateException("No hay oportunidades con modalidad definida");
    }

    /** Primera ubicacion presente entre las oportunidades activas. */
    public String firstLocation(String token) {
        for (JsonNode oportunidad : post("/oportunidades/filtrar", token)) {
            String ubicacion = oportunidad.path("ubicacion").asText();
            if (!ubicacion.isBlank()) {
                return ubicacion;
            }
        }
        throw new IllegalStateException("No hay oportunidades con ubicacion definida");
    }

    /**
     * Crea una oportunidad laboral a nombre del empleador autenticado.
     *
     * <p>Permite que cada caso parta de datos propios y repetibles, en lugar de
     * depender de las oportunidades del seed, que ya pudieron ser postuladas por
     * una corrida anterior.</p>
     */
    public long createOpportunity(String token, long autorId, String titulo, String contenido) {
        JsonNode autor = MAPPER.createObjectNode()
                .put("id", autorId)
                .put("nombre", "Demo Empleador")
                .put("email", "empleador@demo.com");
        JsonNode payload = MAPPER.createObjectNode()
                .put("titulo", titulo)
                .put("contenido", contenido)
                .put("ubicacion", "Centro")
                .put("modalidad", "PRESENCIAL")
                .put("rangoSalarial", "80000-120000")
                .put("activa", true)
                .put("destacada", false)
                .set("autor", autor);
        return post("/oportunidades", payload, token).path("id").asLong();
    }

    /**
     * Cierra una oportunidad (no existe un endpoint de cierre: se hace con un PUT
     * que envia {@code activa=false}).
     */
    public void closeOpportunity(String token, long id, long autorId, String titulo, String contenido) {
        JsonNode autor = MAPPER.createObjectNode()
                .put("id", autorId)
                .put("nombre", "Demo Empleador")
                .put("email", "empleador@demo.com");
        JsonNode payload = MAPPER.createObjectNode()
                .put("titulo", titulo)
                .put("contenido", contenido)
                .put("ubicacion", "Centro")
                .put("modalidad", "PRESENCIAL")
                .put("rangoSalarial", "80000-120000")
                .put("activa", false)
                .put("destacada", false)
                .set("autor", autor);
        put("/oportunidades/" + id, payload, token);
    }

    private JsonNode post(String path, JsonNode payload) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(E2EConfig.apiUrl() + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(20))
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();
        return send(request);
    }

    private JsonNode send(HttpRequest request) {
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException(
                        "La API respondio " + response.statusCode() + ": " + response.body());
            }
            return MAPPER.readTree(response.body());
        } catch (IOException e) {
            throw new UncheckedIOException("Fallo la llamada a " + request.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La llamada a la API fue interrumpida", e);
        }
    }
}
