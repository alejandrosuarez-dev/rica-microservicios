package com.rica.consultasservice.consultas;

import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProyeccionListener {

    private final ProyectorFichas proyector;
    private final ObjectMapper objectMapper;

    public ProyeccionListener(ProyectorFichas proyector, ObjectMapper objectMapper) {
        this.proyector = proyector;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "investigadores.registrados")
    public void investigadorRegistrado(String mensaje) {
        try (MDC.MDCCloseable ignorado = MDC.putCloseable("correlationId", extraerCorrelacion(mensaje))) {
            proyector.alRegistrarseInvestigador(mensaje);
        }
    }

    @KafkaListener(topics = "publicaciones.registradas")
    public void publicacionRegistrada(String mensaje) {
        try (MDC.MDCCloseable ignorado = MDC.putCloseable("correlationId", extraerCorrelacion(mensaje))) {
            proyector.alRegistrarsePublicacion(mensaje);
        }
    }

    @KafkaListener(topics = "investigadores.en-eliminacion")
    public void investigadorEnEliminacion(String mensaje) {
        try (MDC.MDCCloseable ignorado = MDC.putCloseable("correlationId", extraerCorrelacion(mensaje))) {
            proyector.alIniciarseEliminacion(mensaje);
        }
    }

    @KafkaListener(topics = "investigadores.eliminados")
    public void investigadorEliminado(String mensaje) {
        try (MDC.MDCCloseable ignorado = MDC.putCloseable("correlationId", extraerCorrelacion(mensaje))) {
            proyector.alEliminarseInvestigador(mensaje);
        }
    }

    private String extraerCorrelacion(String mensaje) {
        JsonNode nodo = objectMapper.readTree(mensaje);
        return nodo.has("correlationId") ? nodo.get("correlationId").asString() : "sin-correlacion";
    }
}
