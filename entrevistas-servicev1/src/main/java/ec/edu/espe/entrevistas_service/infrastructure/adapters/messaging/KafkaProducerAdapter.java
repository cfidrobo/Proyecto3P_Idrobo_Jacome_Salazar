package ec.edu.espe.entrevistas_service.infrastructure.adapters.messaging;

import ec.edu.espe.entrevistas_service.domain.entities.Entrevista;
import ec.edu.espe.entrevistas_service.domain.entities.Seleccion;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class KafkaProducerAdapter {
    private static final String SELECCION_TOPIC = "seleccion-topic";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplateObject;
    private final ObjectMapper objectMapper;
    private static final String ENTREVISTA_AGENDADA_TOPIC = "entrevista-agendada";

    public KafkaProducerAdapter(KafkaTemplate<String, String> kafkaTemplate, 
                               KafkaTemplate<String, Object> kafkaTemplateObject,
                               ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplateObject = kafkaTemplateObject;
        this.objectMapper = objectMapper;
    }

    public void sendEntrevistaCompletadaEvent(Entrevista entrevista) {
        try {
            String message = objectMapper.writeValueAsString(entrevista);
            kafkaTemplate.send("entrevistas-completadas-topic", message);
            log.info("Evento de entrevista completada enviado: {}", message);
        } catch (Exception e) {
            log.error("Error al enviar evento de entrevista completada: {}", e.getMessage());
        }
    }

    public void sendDecisionFinalEvent(Entrevista entrevista) {
        try {
            String message = objectMapper.writeValueAsString(entrevista);
            kafkaTemplate.send("decisiones-finales-topic", message);
            log.info("Evento de decisión final enviado: {}", message);
        } catch (Exception e) {
            log.error("Error al enviar evento de decisión final: {}", e.getMessage());
        }
    }

    public void sendCandidatoAceptaOfertaEvent(Entrevista entrevista) {
        try {
            String message = objectMapper.writeValueAsString(entrevista);
            kafkaTemplate.send("ofertas-aceptadas-topic", message);
            log.info("Evento de oferta aceptada enviado: {}", message);
        } catch (Exception e) {
            log.error("Error al enviar evento de oferta aceptada: {}", e.getMessage());
        }
    }

    public void sendSeleccionEvent(Seleccion seleccion) {
        try {
            kafkaTemplateObject.send(SELECCION_TOPIC, seleccion);
            log.info("Evento de selección enviado: {}", seleccion);
        } catch (Exception e) {
            log.error("Error al enviar evento de selección: {}", e.getMessage());
            throw new RuntimeException("Error al enviar evento de selección", e);
        }
    }

    public void sendEntrevistaAgendadaEvent(Entrevista entrevista) {
        try {
            String message = objectMapper.writeValueAsString(entrevista);
            kafkaTemplate.send(ENTREVISTA_AGENDADA_TOPIC, message);
            log.info("Evento de entrevista agendada enviado: {}", message);
        } catch (Exception e) {
            log.error("Error al enviar evento de entrevista agendada: {}", e.getMessage());
            throw new RuntimeException("Error al enviar evento de entrevista agendada", e);
        }
    }
}