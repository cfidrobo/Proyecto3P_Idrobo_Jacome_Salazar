package ec.edu.espe.candidatos_service.infrastructure.adapters.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaConsumerAdapter {

    @KafkaListener(topics = "vacantes-topic", groupId = "candidatos-group")
    public void consumeVacanteMessage(String message) {
        try {
            log.info("Mensaje recibido de vacantes: {}", message);
            // Aquí procesas el mensaje de vacantes
        } catch (Exception e) {
            log.error("Error procesando mensaje: {}", e.getMessage());
        }
    }
}