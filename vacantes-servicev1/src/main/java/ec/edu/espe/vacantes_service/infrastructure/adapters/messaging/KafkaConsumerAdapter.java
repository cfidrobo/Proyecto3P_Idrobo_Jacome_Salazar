package ec.edu.espe.vacantes_service.infrastructure.adapters.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import ec.edu.espe.vacantes_service.application.services.RequisicionService;
import ec.edu.espe.vacantes_service.infrastructure.dtos.CandidatoMessage;

@Component
@Slf4j
public class KafkaConsumerAdapter {
    private final RequisicionService requisicionService;

    public KafkaConsumerAdapter(RequisicionService requisicionService) {
        this.requisicionService = requisicionService;
    }

    @KafkaListener(topics = "vacante-candidatos-topic", groupId = "vacantes-group")
    public void consumeCandidatoMessage(CandidatoMessage message) {
        try {
            log.info("Candidato recibido para vacante: {}", message);
            requisicionService.procesarPostulacionCandidato(message);
        } catch (Exception e) {
            log.error("Error procesando candidato: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "candidato-finalista-topic", groupId = "vacantes-group")
    public void consumeCandidatoFinalistaMessage(CandidatoMessage message) {
        try {
            log.info("Candidato finalista recibido: {}", message);
            requisicionService.procesarCandidatoFinalista(message);
        } catch (Exception e) {
            log.error("Error procesando candidato finalista: {}", e.getMessage());
        }
    }
}