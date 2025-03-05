package ec.edu.espe.vacantes_service.infrastructure.adapters.bpmn;

import ec.edu.espe.vacantes_service.application.services.RequisicionService;
import ec.edu.espe.vacantes_service.domain.entities.Requisicion;
import ec.edu.espe.vacantes_service.infrastructure.adapters.messaging.KafkaProducerAdapter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component("publicarVacanteDelegate")
@Slf4j
public class PublicarVacanteDelegate implements JavaDelegate {

    private final RequisicionService requisicionService;
    private final KafkaProducerAdapter kafkaProducer;

    public PublicarVacanteDelegate(
            RequisicionService requisicionService,
            KafkaProducerAdapter kafkaProducer) {
        this.requisicionService = requisicionService;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        try {
            Long requisicionId = (Long) execution.getVariable("requisicionId");
            Requisicion requisicion = requisicionService.findById(requisicionId);
            
            // Publicar la vacante vía Kafka
            kafkaProducer.sendVacantePublishedEvent(requisicion);
            log.info("Vacante publicada para requisición: {}", requisicionId);
            
            // Establecer variable de proceso
            execution.setVariable("vacantePublicada", true);
        } catch (Exception e) {
            log.error("Error al publicar vacante: {}", e.getMessage());
            execution.setVariable("vacantePublicada", false);
            throw e;
        }
    }
}