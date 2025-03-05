package ec.edu.espe.entrevistas_service.infrastructure.adapters.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaConsumerAdapter {

    @KafkaListener(topics = "candidatos-topic", groupId = "entrevistas-group")
    public void consumeCandidatoMessage(String message) {
        try {
            log.info("Mensaje recibido de candidatos: {}", message);
            // Aquí procesas el mensaje del candidato
            // Por ejemplo, podrías programar una entrevista automáticamente
        } catch (Exception e) {
            log.error("Error procesando mensaje de candidato: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "vacantes-topic", groupId = "entrevistas-group")
    public void consumeVacanteMessage(String message) {
        try {
            log.info("Mensaje recibido de vacantes: {}", message);
            // Aquí procesas el mensaje de la vacante
        } catch (Exception e) {
            log.error("Error procesando mensaje de vacante: {}", e.getMessage());
        }
    }
}