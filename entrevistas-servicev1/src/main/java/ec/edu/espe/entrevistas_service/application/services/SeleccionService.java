package ec.edu.espe.entrevistas_service.application.services;

import ec.edu.espe.entrevistas_service.domain.entities.Seleccion;
import ec.edu.espe.entrevistas_service.application.usecases.SeleccionarCandidatoUseCase;
import ec.edu.espe.entrevistas_service.domain.repositories.SeleccionRepository;
import ec.edu.espe.entrevistas_service.infrastructure.adapters.messaging.KafkaProducerAdapter;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Service
@Slf4j
public class SeleccionService {

    private final SeleccionarCandidatoUseCase seleccionarCandidatoUseCase;
    private final SeleccionRepository seleccionRepository;
    private final KafkaProducerAdapter kafkaProducer;

    public SeleccionService(
            SeleccionarCandidatoUseCase seleccionarCandidatoUseCase, 
            SeleccionRepository seleccionRepository,
            KafkaProducerAdapter kafkaProducer) {
        this.seleccionarCandidatoUseCase = seleccionarCandidatoUseCase;
        this.seleccionRepository = seleccionRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public Seleccion seleccionarCandidato(Long candidatoId, String resultado) {
        try {
            Seleccion seleccion = seleccionarCandidatoUseCase.execute(candidatoId, resultado);
            // Enviar evento a Kafka
            kafkaProducer.sendSeleccionEvent(seleccion);
            log.info("Selección realizada y notificada: {}", seleccion);
            return seleccion;
        } catch (Exception e) {
            log.error("Error al procesar la selección del candidato: {}", e.getMessage());
            throw e;
        }
    }

    public List<Seleccion> findAll() {
        return seleccionRepository.findAll();
    }

    public Seleccion findById(Long id) {
        return seleccionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Selección no encontrada con ID: " + id));
    }

    public List<Seleccion> findByCandidatoId(Long candidatoId) {
        return seleccionRepository.findByCandidatoId(candidatoId);
    }
}