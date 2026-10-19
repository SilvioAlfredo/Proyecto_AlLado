package com.al_lado.al_lado_project.exception;

import com.al_lado.al_lado_project.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador MVC central: traduce las excepciones del dominio y de Spring a
 * respuestas {@link ErrorResponse} con su código HTTP.
 *
 * <p>Política de códigos:</p>
 * <ul>
 *   <li><b>400</b> — {@link BusinessRuleException} (regla de negocio violada),
 *       {@link MethodArgumentNotValidException} (Bean Validation, con detalle campo
 *       a campo) y {@link MissingServletRequestParameterException} (parámetro
 *       ausente).</li>
 *   <li><b>401</b> — {@link BadCredentialsException} y cualquier
 *       {@link org.springframework.security.core.AuthenticationException}
 *       (cuenta deshabilitada u otras).</li>
 *   <li><b>403</b> — {@link AccessDeniedException} (rol o titularidad).</li>
 *   <li><b>404</b> — {@link ResourceNotFoundException}.</li>
 *   <li><b>409</b> — {@link DuplicateResourceException} y
 *       {@link DataIntegrityViolationException} no anticipada, con un mensaje
 *       <b>genérico</b> que no filtra el SQL.</li>
 *   <li><b>500</b> — todo lo no contemplado, con mensaje genérico.</li>
 * </ul>
 *
 * <p><b>Límites de este manejador</b> (es un {@code @RestControllerAdvice} de MVC):</p>
 * <ul>
 *   <li>Solo ve excepciones que llegan al controlador. Lo que falla antes —en
 *       particular {@code JwtAuthenticationFilter}— responde sin pasar por aquí.</li>
 *   <li>No tiene handler para {@link IllegalArgumentException}: los rechazos de
 *       {@code AuthService} (token inválido, contraseña actual incorrecta) caen en
 *       el genérico y responden <b>500</b>, no 400.</li>
 *   <li>No traduce automáticamente las violaciones de integridad a una regla de
 *       negocio: depende de cuándo se materialicen, por lo que el código puede ser
 *       400 o 409 según el caso.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Manejo de recurso no encontrado (HTTP 404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 2. Manejo de conflicto por duplicidad (HTTP 409)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 2b. Manejo de violaciones de integridad no previstas (HTTP 409).
    // Los flujos conocidos la convierten antes a BusinessRuleException (400);
    // esto cubre carreras residuales con un 409 genérico sin filtrar SQL.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message("La operación viola una restricción de integridad de datos.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 3. Manejo de violaciones de reglas de negocio (HTTP 400)
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(BusinessRuleException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 4. Manejo de errores de validación DTO de Bean Validation (@Valid) (HTTP 400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Existen errores en los datos enviados en la petición.")
                .errors(errors)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 4b. Parámetro requerido ausente en query/path (HTTP 400 en vez de 500)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Falta el parámetro requerido: " + ex.getParameterName() + ".")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 5. Manejo de errores de autenticación Spring Security (HTTP 401)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Credenciales de acceso inválidas.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // 6. Manejo de acceso denegado por roles (HTTP 403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("No tiene permisos suficientes para acceder a este recurso.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // 5b. Manejo de fallos de autenticación de cuenta (deshabilitada, bloqueada, expirada) (HTTP 401)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Credenciales de acceso inválidas o cuenta no habilitada.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // 7. Captura de cualquier otra excepción no contemplada (HTTP 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocurrió un error inesperado en el servidor.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}