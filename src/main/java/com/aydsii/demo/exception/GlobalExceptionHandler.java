package com.aydsii.demo.exception;

import com.aydsii.demo.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/** formato {status, message, data}. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // SLF4J: la clase solo conoce la interfaz Logger; la implementación (Log4j2) la elige el pom.xml
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        return responder(HttpStatus.BAD_REQUEST, "Error de validación", errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es válido (revisá el JSON y que tipoEntrega sea DOMICILIO o RETIRO)", null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> manejarParametroFaltante(MissingServletRequestParameterException ex) {
        return responder(HttpStatus.BAD_REQUEST, "Falta el parámetro obligatorio: " + ex.getParameterName(), null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return responder(HttpStatus.BAD_REQUEST, "El parámetro " + ex.getName() + " tiene un formato inválido", null);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiResponse<Object>> manejarReglaNegocio(ReglaNegocioException ex) {
        log.warn("Regla de negocio no cumplida: {}", ex.getMessage());
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Object>> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> manejarRutaInexistente(NoResourceFoundException ex) {
        return responder(HttpStatus.NOT_FOUND, "La ruta solicitada no existe", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> manejarErrorGeneral(Exception ex) {
        log.error("Error no controlado", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", null);
    }

    private <T> ResponseEntity<ApiResponse<T>> responder(HttpStatus status, String mensaje, T data) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status.value(), mensaje, data));
    }
}
