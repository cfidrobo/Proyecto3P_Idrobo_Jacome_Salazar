package ec.edu.espe.vacantes_service.infrastructure.adapters.messaging;

import ec.edu.espe.vacantes_service.domain.entities.Requisicion;
import ec.edu.espe.vacantes_service.infrastructure.dtos.VacanteMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaProducerAdapter {
    private final KafkaTemplate<String, VacanteMessage> kafkaTemplate;
    private static final String TOPIC_VACANTES = "vacantes-topic";
    private static final String TOPIC_VACANTE_CERRADA = "vacante-cerrada";

    public KafkaProducerAdapter(KafkaTemplate<String, VacanteMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendVacantePublishedEvent(Requisicion requisicion) {
        try {
            VacanteMessage message = VacanteMessage.builder()
                .id(requisicion.getId())
                .cargo(requisicion.getCargo())
                .descripcion(requisicion.getDescripcion())
                .categoriaSalarial(requisicion.getCategoriaSalarial())
                .perfil(requisicion.getPerfil())
                .fechaCreacion(requisicion.getFechaCreacion())
                .fechaLimiteConvocatoria(requisicion.getFechaLimiteConvocatoria())
                .estado(requisicion.getEstado().toString())
                .tipoReclutamiento(requisicion.getTipoReclutamiento().toString())
                .aprobadoPorRRHH(requisicion.isAprobadoPorRRHH())
                .build();
            
            CompletableFuture<SendResult<String, VacanteMessage>> future = 
                kafkaTemplate.send(TOPIC_VACANTES, requisicion.getId().toString(), message);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Mensaje de vacante publicada enviado: [topic: {}, partition: {}, offset: {}]",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                } else {
                    log.error("Error al enviar mensaje de vacante publicada: {}", ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error al enviar mensaje de vacante publicada: {}", e.getMessage());
            throw new RuntimeException("Error al publicar vacante en Kafka", e);
        }
    }

    public void sendVacanteCerradaEvent(Requisicion requisicion) {
        try {
            VacanteMessage message = VacanteMessage.builder()
                .id(requisicion.getId())
                .cargo(requisicion.getCargo())
                .descripcion(requisicion.getDescripcion())
                .categoriaSalarial(requisicion.getCategoriaSalarial())
                .perfil(requisicion.getPerfil())
                .fechaCreacion(requisicion.getFechaCreacion())
                .fechaLimiteConvocatoria(requisicion.getFechaLimiteConvocatoria())
                .estado("CERRADA")
                .tipoReclutamiento(requisicion.getTipoReclutamiento().toString())
                .aprobadoPorRRHH(requisicion.isAprobadoPorRRHH())
                .build();
            
            CompletableFuture<SendResult<String, VacanteMessage>> future = 
                kafkaTemplate.send(TOPIC_VACANTE_CERRADA, requisicion.getId().toString(), message);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Mensaje de vacante cerrada enviado: [topic: {}, partition: {}, offset: {}]",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                } else {
                    log.error("Error al enviar mensaje de vacante cerrada: {}", ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error al enviar mensaje de vacante cerrada: {}", e.getMessage());
            throw new RuntimeException("Error al publicar cierre de vacante en Kafka", e);
        }
    }
}