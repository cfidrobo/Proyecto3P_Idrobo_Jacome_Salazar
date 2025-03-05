package ec.edu.espe.entrevistas_service.application.services;

import ec.edu.espe.entrevistas_service.domain.entities.Entrevista;
import ec.edu.espe.entrevistas_service.domain.entities.TipoEntrevista;
import ec.edu.espe.entrevistas_service.domain.entities.EstadoDecision;
import ec.edu.espe.entrevistas_service.domain.entities.Candidato;
import ec.edu.espe.entrevistas_service.domain.repositories.EntrevistaRepository;
import ec.edu.espe.entrevistas_service.infrastructure.adapters.messaging.KafkaProducerAdapter;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EntrevistaService {
    private final EntrevistaRepository entrevistaRepository;
    private final CandidatoService candidatoService;
    private final KafkaProducerAdapter kafkaProducer;

    public EntrevistaService(EntrevistaRepository entrevistaRepository, 
                           CandidatoService candidatoService,
                           KafkaProducerAdapter kafkaProducer) {
        this.entrevistaRepository = entrevistaRepository;
        this.candidatoService = candidatoService;
        this.kafkaProducer = kafkaProducer;
    }

    public List<Entrevista> findAll() {
        return entrevistaRepository.findAll();
    }

    public Entrevista findById(Long id) {
        return entrevistaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Entrevista no encontrada: " + id));
    }

    public Entrevista agendarEntrevista(Entrevista entrevista) {
        try {
            log.info("Agendando entrevista para candidato: {}", entrevista.getCandidatoId());
            
            // Validar que el candidato tenga una postulación activa
            Candidato candidato = candidatoService.getCandidatoById(entrevista.getCandidatoId());
            if (candidato == null || candidato.getVacanteId() == null) {
                throw new RuntimeException("El candidato debe tener una postulación activa para agendar una entrevista");
            }

            // Validar que no exista otra entrevista del mismo tipo pendiente
            validarEntrevistaPendiente(entrevista.getCandidatoId(), entrevista.getTipo());

            // Establecer estado inicial
            entrevista.setEstado("PROGRAMADA");
            
            // Guardar la entrevista
            Entrevista entrevistaGuardada = entrevistaRepository.save(entrevista);
            
            // Enviar evento de entrevista agendada
            kafkaProducer.sendEntrevistaAgendadaEvent(entrevistaGuardada);
            
            return entrevistaGuardada;
        } catch (Exception e) {
            log.error("Error al agendar entrevista: ", e);
            throw new RuntimeException("Error al agendar entrevista: " + e.getMessage());
        }
    }

    public Entrevista registrarCalificacion(Long entrevistaId, Double calificacion, 
                                          Boolean aprobada, String comentarios) {
        log.info("Registrando calificación para entrevista: {}", entrevistaId);
        Entrevista entrevista = findById(entrevistaId);
        
        entrevista.setCalificacion(calificacion);
        entrevista.setAprobada(aprobada);
        entrevista.setComentarios(comentarios);
        entrevista.setFechaDecision(LocalDateTime.now());
        
        Entrevista entrevistaActualizada = entrevistaRepository.save(entrevista);
        kafkaProducer.sendEntrevistaCompletadaEvent(entrevistaActualizada);
        
        return entrevistaActualizada;
    }

    public Entrevista actualizarEstadoDecision(Long entrevistaId, EstadoDecision estado, 
                                              String comentarios, String areaAprobadora) {
        log.info("Actualizando estado de decisión para entrevista: {}", entrevistaId);
        Entrevista entrevista = findById(entrevistaId);
        
        entrevista.setEstadoDecision(estado);
        entrevista.setComentariosDecision(comentarios);
        entrevista.setAreaAprobadora(areaAprobadora);
        entrevista.setFechaDecision(LocalDateTime.now());
        
        Entrevista entrevistaActualizada = entrevistaRepository.save(entrevista);
        
        if (estado == EstadoDecision.SELECCIONADO || estado == EstadoDecision.RECHAZADO) {
            kafkaProducer.sendDecisionFinalEvent(entrevistaActualizada);
        } else if (estado == EstadoDecision.ACEPTADO_POR_CANDIDATO) {
            kafkaProducer.sendCandidatoAceptaOfertaEvent(entrevistaActualizada);
        }
        
        return entrevistaActualizada;
    }

    public List<Entrevista> findByTipo(TipoEntrevista tipo) {
        log.info("Buscando entrevistas de tipo: {}", tipo);
        return entrevistaRepository.findByTipo(tipo);
    }

    private void validarEntrevistaPendiente(Long candidatoId, TipoEntrevista tipo) {
        entrevistaRepository.findByCandidatoIdAndTipoAndEstado(candidatoId, tipo, "PROGRAMADA")
            .ifPresent(e -> {
                throw new RuntimeException("Ya existe una entrevista programada de tipo " + 
                    tipo + " para el candidato " + candidatoId);
            });
    }
}