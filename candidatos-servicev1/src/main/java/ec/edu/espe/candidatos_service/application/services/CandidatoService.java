package ec.edu.espe.candidatos_service.application.services;

import ec.edu.espe.candidatos_service.domain.entities.Candidato;
import ec.edu.espe.candidatos_service.domain.entities.EstadoCandidato;
import ec.edu.espe.candidatos_service.domain.entities.TipoCandidato;
import ec.edu.espe.candidatos_service.domain.repositories.CandidatoRepository;
import ec.edu.espe.candidatos_service.infrastructure.adapters.messaging.KafkaProducerAdapter;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CandidatoService {
    private static final double PUNTAJE_MINIMO = 70.0;
    
    private final CandidatoRepository candidatoRepository;
    private final KafkaProducerAdapter kafkaProducer;

    public CandidatoService(CandidatoRepository candidatoRepository, 
                           KafkaProducerAdapter kafkaProducer) {
        this.candidatoRepository = candidatoRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public List<Candidato> findAll() {
        try {
            log.info("Buscando todos los candidatos");
            return candidatoRepository.findAll();
        } catch (Exception e) {
            log.error("Error al buscar candidatos: {}", e.getMessage());
            throw new RuntimeException("Error al buscar candidatos", e);
        }
    }

    public Candidato findById(Long id) {
        log.info("Buscando candidato con ID: {}", id);
        return candidatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado con ID: " + id));
    }

    public List<Candidato> findByEstado(EstadoCandidato estado) {
        log.info("Buscando candidatos en estado: {}", estado);
        return candidatoRepository.findByEstado(estado);
    }

    public List<Candidato> findByTipo(TipoCandidato tipo) {
        log.info("Buscando candidatos de tipo: {}", tipo);
        return candidatoRepository.findByTipo(tipo);
    }

    public Candidato createCandidato(Candidato candidato) {
        log.info("Creando nuevo candidato: {}", candidato.getNombre());
        candidato.setEstado(EstadoCandidato.POSTULADO);
        candidato.setFechaPostulacion(LocalDateTime.now());
        return candidatoRepository.save(candidato);
    }

    public Candidato registrarEvaluacionPsicotecnica(Long id, Double puntaje, String observaciones) {
        log.info("Registrando evaluación psicotécnica para candidato ID: {}", id);
        Candidato candidato = findById(id);
        
        if (candidato.getEstado() != EstadoCandidato.POSTULADO) {
            throw new IllegalStateException("El candidato debe estar en estado POSTULADO");
        }

        candidato.setPuntajePsicotecnico(puntaje);
        candidato.setObservaciones(observaciones);
        candidato.setEstado(EstadoCandidato.EN_EVALUACION_TECNICA);
        candidato.setFechaUltimaEvaluacion(LocalDateTime.now());
        candidato.calcularPuntajeTotal();
        
        Candidato candidatoActualizado = candidatoRepository.save(candidato);
        kafkaProducer.sendEvaluacionPsicotecnicaCompletada(candidatoActualizado);
        
        return candidatoActualizado;
    }

    public Candidato registrarEvaluacionTecnica(Long id, Double puntaje, String observaciones) {
        log.info("Registrando evaluación técnica para candidato ID: {}", id);
        Candidato candidato = findById(id);
        
        if (candidato.getEstado() != EstadoCandidato.EN_EVALUACION_TECNICA) {
            throw new IllegalStateException("El candidato debe estar en estado EN_EVALUACION_TECNICA");
        }

        candidato.setPuntajeTecnico(puntaje);
        candidato.setObservaciones(candidato.getObservaciones() + "\n" + observaciones);
        candidato.setFechaUltimaEvaluacion(LocalDateTime.now());
        candidato.calcularPuntajeTotal();
        
        if (cumpleRequisitosMinimos(candidato)) {
            candidato.setEstado(EstadoCandidato.FINALISTA);
            kafkaProducer.sendCandidatoFinalistaEvent(candidato);
        } else {
            candidato.setEstado(EstadoCandidato.RECHAZADO);
        }
        
        return candidatoRepository.save(candidato);
    }

    public Candidato postularAVacante(Long candidatoId, Long vacanteId) {
        log.info("Postulando candidato {} a vacante {}", candidatoId, vacanteId);
        Candidato candidato = findById(candidatoId);
        
        if (candidato.getEstado() != EstadoCandidato.POSTULADO) {
            throw new IllegalStateException(
                "El candidato debe estar en estado POSTULADO. Estado actual: " + candidato.getEstado());
        }
        if (candidato.getVacanteId() != null) {
            throw new IllegalStateException(
                "El candidato ya está postulado a la vacante: " + candidato.getVacanteId());
        }
        candidato.setVacanteId(vacanteId);
        Candidato candidatoActualizado = candidatoRepository.save(candidato);
        try {
            kafkaProducer.sendCandidatoToVacante(candidatoActualizado);
        } catch (Exception e) {
            log.error("Error al enviar mensaje a Kafka: {}", e.getMessage());
            candidato.setVacanteId(null);
            return candidatoRepository.save(candidato);
        }        
        return candidatoActualizado;
    }

    public Candidato actualizarEstadoPostulacion(Long id, EstadoCandidato nuevoEstado, String observaciones) {
        log.info("Actualizando estado de postulación para candidato {}", id);
        Candidato candidato = findById(id);
        
        validarTransicionEstado(candidato.getEstado(), nuevoEstado);
        
        candidato.setEstado(nuevoEstado);
        candidato.setObservaciones(candidato.getObservaciones() + "\n" + observaciones);
        candidato.setFechaUltimaEvaluacion(LocalDateTime.now());
        
        return candidatoRepository.save(candidato);
    }

    public Candidato registrarEntrevista(Long id, Double puntaje, String observaciones) {
        log.info("Registrando entrevista para candidato ID: {}", id);
        Candidato candidato = findById(id);
        
        if (candidato.getEstado() != EstadoCandidato.FINALISTA) {
            throw new IllegalStateException("El candidato debe estar en estado FINALISTA");
        }

        candidato.setPuntajeEntrevista(puntaje);
        candidato.setObservaciones(candidato.getObservaciones() + "\n" + observaciones);
        candidato.setFechaUltimaEvaluacion(LocalDateTime.now());
        candidato.calcularPuntajeTotal();
        
        return candidatoRepository.save(candidato);
    }

    public Candidato seleccionarCandidato(Long id) {
        log.info("Seleccionando candidato ID: {}", id);
        Candidato candidato = findById(id);
        
        if (candidato.getEstado() != EstadoCandidato.FINALISTA) {
            throw new IllegalStateException("El candidato debe estar en estado FINALISTA");
        }

        candidato.setEstado(EstadoCandidato.SELECCIONADO);
        candidato.setFechaUltimaEvaluacion(LocalDateTime.now());
        
        Candidato candidatoSeleccionado = candidatoRepository.save(candidato);
        kafkaProducer.sendCandidatoSeleccionadoEvent(candidatoSeleccionado);
        
        return candidatoSeleccionado;
    }

    public Candidato rechazarCandidato(Long id, String motivo) {
        log.info("Rechazando candidato ID: {}", id);
        Candidato candidato = findById(id);
        candidato.setEstado(EstadoCandidato.RECHAZADO);
        candidato.setObservaciones(candidato.getObservaciones() + "\nMotivo de rechazo: " + motivo);
        candidato.setFechaUltimaEvaluacion(LocalDateTime.now());
        
        return candidatoRepository.save(candidato);
    }

    private boolean cumpleRequisitosMinimos(Candidato candidato) {
        return candidato.getPuntajePsicotecnico() >= PUNTAJE_MINIMO &&
               candidato.getPuntajeTecnico() >= PUNTAJE_MINIMO;
    }
    private void validarTransicionEstado(EstadoCandidato estadoActual, EstadoCandidato nuevoEstado) {
        
        if (estadoActual == EstadoCandidato.RECHAZADO) {
            throw new IllegalStateException("No se puede cambiar el estado de un candidato RECHAZADO");
        }
    }
}