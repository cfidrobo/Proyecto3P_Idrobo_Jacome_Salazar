package ec.edu.espe.candidatos_service.infrastructure.adapters.messaging;

import ec.edu.espe.candidatos_service.domain.entities.Candidato;
import ec.edu.espe.candidatos_service.infrastructure.dtos.CandidatoMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaProducerAdapter {
    private static final String EVALUACION_PSICOTECNICA_TOPIC = "evaluacion-psicotecnica-topic";
    private static final String CANDIDATO_FINALISTA_TOPIC = "candidato-finalista-topic";
    private static final String CANDIDATO_SELECCIONADO_TOPIC = "candidato-seleccionado-topic";
    private static final String VACANTE_CANDIDATOS_TOPIC = "vacante-candidatos-topic";
    
    private final KafkaTemplate<String, CandidatoMessage> kafkaTemplate;

    public KafkaProducerAdapter(KafkaTemplate<String, CandidatoMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvaluacionPsicotecnicaCompletada(Candidato candidato) {
        CandidatoMessage message = buildCandidatoMessage(candidato);
        send(EVALUACION_PSICOTECNICA_TOPIC, message);
    }

    public void sendCandidatoFinalistaEvent(Candidato candidato) {
        CandidatoMessage message = buildCandidatoMessage(candidato);
        send(CANDIDATO_FINALISTA_TOPIC, message);
    }

    public void sendCandidatoSeleccionadoEvent(Candidato candidato) {
        CandidatoMessage message = buildCandidatoMessage(candidato);
        send(CANDIDATO_SELECCIONADO_TOPIC, message);
    }

    public void sendCandidatoToVacante(Candidato candidato) {
        CandidatoMessage message = buildCandidatoMessage(candidato);
        send(VACANTE_CANDIDATOS_TOPIC, message);
    }

    private CandidatoMessage buildCandidatoMessage(Candidato candidato) {
        return new CandidatoMessage(
            candidato.getId(),
            candidato.getNombre(),
            candidato.getEstado().toString(),
            candidato.getPuntajeTotal(),
            candidato.getExperiencia(),
            candidato.getHabilidades(),
            candidato.getVacanteId(),
            candidato.getTipo().toString()
        );
    }

    private void send(String topic, CandidatoMessage message) {
        try {
            kafkaTemplate.send(topic, message);
            log.info("Mensaje enviado a {}: {}", topic, message);
        } catch (Exception e) {
            log.error("Error enviando mensaje a {}: {}", topic, e.getMessage());
        }
    }
}