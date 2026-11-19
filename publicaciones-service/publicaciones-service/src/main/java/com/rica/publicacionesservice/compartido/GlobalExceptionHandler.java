package com.rica.publicacionesservice.compartido;

import com.rica.publicacionesservice.publicaciones.ServicioNoDisponibleException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<Map<String, Object>> manejarServicioNoDisponible(
            ServicioNoDisponibleException ex) {

        return construirRespuesta(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage()
        );
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus estado, String mensaje) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("status", estado.value());
        respuesta.put("error", estado.getReasonPhrase());
        respuesta.put("mensaje", mensaje);

        return ResponseEntity.status(estado).body(respuesta);
    }
}