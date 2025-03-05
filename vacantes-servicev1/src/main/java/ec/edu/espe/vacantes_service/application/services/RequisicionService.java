package ec.edu.espe.vacantes_service.application.services;

import ec.edu.espe.vacantes_service.domain.entities.Requisicion;
import ec.edu.espe.vacantes_service.domain.entities.EstadoVacante;
import ec.edu.espe.vacantes_service.domain.repositories.RequisicionRepository;
import ec.edu.espe.vacantes_service.infrastructure.adapters.messaging.KafkaProducerAdapter;
import ec.edu.espe.vacantes_service.infrastructure.dtos.CandidatoMessage;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
@Slf4j
public class RequisicionService {
    private final RequisicionRepository requisicionRepository;
    private final KafkaProducerAdapter kafkaProducer;
    private final Map<Long, List<CandidatoMessage>> candidatosPorVacante;

    public RequisicionService(RequisicionRepository requisicionRepository, 
                            KafkaProducerAdapter kafkaProducer) {
        this.requisicionRepository = requisicionRepository;
        this.kafkaProducer = kafkaProducer;
        this.candidatosPorVacante = new HashMap<>();
    }

    public Requisicion createRequisicion(Requisicion requisicion) {
        log.info("Iniciando creación de requisición");
        
        if (!requisicion.isInformacionCompleta()) {
            log.error("Información de requisición incompleta");
            throw new IllegalArgumentException("La información de la requisición está incompleta");
        }

        requisicion.setEstado(EstadoVacante.BORRADOR);
        requisicion.setFechaCreacion(LocalDateTime.now());
        requisicion.setAprobadoPorRRHH(false);

        log.info("Guardando nueva requisición");
        Requisicion savedRequisicion = requisicionRepository.save(requisicion);
        log.info("Requisición creada con ID: {}", savedRequisicion.getId());
        
        return savedRequisicion;
    }

    public boolean deleteRequisicion(Long id) {
        log.info("Eliminando requisición con ID: {}", id);
        try {
            requisicionRepository.deleteById(id);
            log.info("Requisición {} eliminada exitosamente", id);
            return true;
        } catch (Exception e) {
            log.error("Error al eliminar requisición {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al eliminar requisición: " + e.getMessage());
        }
    }

    public Requisicion aprobarRequisicion(Long id) {
        log.info("Iniciando aprobación de requisición ID: {}", id);
        
        Requisicion requisicion = findById(id);
        validarCambioEstado(requisicion, EstadoVacante.PUBLICADA);

        requisicion.setAprobadoPorRRHH(true);
        requisicion.setEstado(EstadoVacante.PUBLICADA);
        
        Requisicion requisicionAprobada = requisicionRepository.save(requisicion);
        log.info("Requisición {} aprobada exitosamente", id);
        
        try {
            kafkaProducer.sendVacantePublishedEvent(requisicionAprobada);
            log.info("Evento de publicación enviado para requisición {}", id);
        } catch (Exception e) {
            log.error("Error al enviar evento de publicación para requisición {}", id, e);
        }
        
        return requisicionAprobada;
    }

    public void procesarPostulacionCandidato(CandidatoMessage candidato) {
        log.info("Procesando postulación de candidato: {}", candidato);
        Requisicion requisicion = findById(candidato.getVacanteId());
        
        if (requisicion.getEstado() != EstadoVacante.PUBLICADA) {
            log.error("La vacante {} no está disponible para postulaciones", candidato.getVacanteId());
            throw new IllegalStateException("La vacante no está disponible para postulaciones");
        }

        // Agregar candidato a la lista de postulantes
        List<CandidatoMessage> candidatos = candidatosPorVacante.getOrDefault(
            requisicion.getId(), new ArrayList<>());
        candidatos.add(candidato);
        candidatosPorVacante.put(requisicion.getId(), candidatos);

        log.info("Candidato {} agregado a la vacante {}", candidato.getId(), requisicion.getId());
    }

    public void procesarCandidatoFinalista(CandidatoMessage candidato) {
        log.info("Procesando candidato finalista: {}", candidato);
        Requisicion requisicion = findById(candidato.getVacanteId());
        
        if (requisicion.getEstado() != EstadoVacante.PUBLICADA) {
            log.error("La vacante {} no está en estado válido para finalistas", candidato.getVacanteId());
            return;
        }

        // Actualizar información del finalista en la requisición
        requisicion.agregarFinalista(candidato.getId());
        requisicionRepository.save(requisicion);
        
        log.info("Candidato finalista {} registrado para vacante {}", 
            candidato.getId(), requisicion.getId());
    }

    public List<CandidatoMessage> obtenerCandidatosPorVacante(Long vacanteId) {
        log.info("Obteniendo candidatos para vacante: {}", vacanteId);
        return candidatosPorVacante.getOrDefault(vacanteId, new ArrayList<>());
    }

    public Requisicion enviarParaAprobacion(Long id) {
        log.info("Enviando requisición {} para aprobación", id);
        
        Requisicion requisicion = findById(id);
        validarCambioEstado(requisicion, EstadoVacante.PENDIENTE_APROBACION);

        requisicion.setEstado(EstadoVacante.PENDIENTE_APROBACION);
        Requisicion requisicionEnviada = requisicionRepository.save(requisicion);
        log.info("Requisición {} enviada para aprobación exitosamente", id);
        
        return requisicionEnviada;
    }

    public Requisicion cerrarVacante(Long id) {
        log.info("Iniciando cierre de vacante ID: {}", id);
        
        Requisicion requisicion = findById(id);
        validarCambioEstado(requisicion, EstadoVacante.CERRADA);

        requisicion.setEstado(EstadoVacante.CERRADA);
        Requisicion requisicionCerrada = requisicionRepository.save(requisicion);
        log.info("Vacante {} cerrada exitosamente", id);
        
        try {
            kafkaProducer.sendVacanteCerradaEvent(requisicionCerrada);
            log.info("Evento de cierre enviado para vacante {}", id);
        } catch (Exception e) {
            log.error("Error al enviar evento de cierre para vacante {}", id, e);
        }
        
        return requisicionCerrada;
    }

    public Requisicion updateRequisicion(Long id, Requisicion requisicionActualizada) {
        log.info("Actualizando requisición ID: {}", id);
        
        Requisicion requisicionExistente = findById(id);
        
        if (requisicionExistente.getEstado() == EstadoVacante.PUBLICADA || 
            requisicionExistente.getEstado() == EstadoVacante.CERRADA) {
            log.error("No se puede actualizar una requisición {} en estado {}", 
                id, requisicionExistente.getEstado());
            throw new IllegalStateException(
                "No se puede actualizar una requisición en estado " + 
                requisicionExistente.getEstado());
        }
    
        actualizarCamposRequisicion(requisicionExistente, requisicionActualizada);
        
        log.info("Guardando requisición actualizada");
        return requisicionRepository.save(requisicionExistente);
    }

    private void actualizarCamposRequisicion(Requisicion existente, Requisicion actualizada) {
        if (actualizada.getCargo() != null) {
            existente.setCargo(actualizada.getCargo());
        }
        if (actualizada.getDescripcion() != null) {
            existente.setDescripcion(actualizada.getDescripcion());
        }
        if (actualizada.getCategoriaSalarial() != null) {
            existente.setCategoriaSalarial(actualizada.getCategoriaSalarial());
        }
        if (actualizada.getPerfil() != null) {
            existente.setPerfil(actualizada.getPerfil());
        }
        if (actualizada.getFechaLimiteConvocatoria() != null) {
            existente.setFechaLimiteConvocatoria(actualizada.getFechaLimiteConvocatoria());
        }
        if (actualizada.getTipoReclutamiento() != null) {
            existente.setTipoReclutamiento(actualizada.getTipoReclutamiento());
        }
    }

    @Scheduled(cron = "0 0 * * * *") // Cada hora
    public void verificarFechasLimite() {
        log.info("Iniciando verificación de fechas límite");
        
        List<Requisicion> requisicionesPublicadas = requisicionRepository.findByEstado(EstadoVacante.PUBLICADA);
        LocalDateTime now = LocalDateTime.now();
        
        for (Requisicion requisicion : requisicionesPublicadas) {
            if (requisicion.getFechaLimiteConvocatoria().isBefore(now)) {
                log.info("Cerrando automáticamente vacante {} por fecha límite", requisicion.getId());
                cerrarVacante(requisicion.getId());
            }
        }
        
        log.info("Verificación de fechas límite completada");
    }

    private void validarCambioEstado(Requisicion requisicion, EstadoVacante nuevoEstado) {
        EstadoVacante estadoActual = requisicion.getEstado();
        
        if (estadoActual == nuevoEstado) {
            log.error("Intento de cambio al mismo estado: {}", nuevoEstado);
            throw new IllegalStateException("La requisición ya se encuentra en estado " + nuevoEstado);
        }

        switch (nuevoEstado) {
            case PENDIENTE_APROBACION:
                if (estadoActual != EstadoVacante.BORRADOR) {
                    log.error("Intento inválido de cambio de estado: {} -> {}", estadoActual, nuevoEstado);
                    throw new IllegalStateException("Solo se pueden enviar a aprobación requisiciones en estado BORRADOR");
                }
                break;
            case PUBLICADA:
                if (estadoActual != EstadoVacante.PENDIENTE_APROBACION) {
                    log.error("Intento inválido de cambio de estado: {} -> {}", estadoActual, nuevoEstado);
                    throw new IllegalStateException("Solo se pueden publicar requisiciones en estado PENDIENTE_APROBACION");
                }
                break;
            case CERRADA:
                if (estadoActual != EstadoVacante.PUBLICADA) {
                    log.error("Intento inválido de cambio de estado: {} -> {}", estadoActual, nuevoEstado);
                    throw new IllegalStateException("Solo se pueden cerrar requisiciones publicadas");
                }
                break;
            default:
                log.error("Estado no válido: {}", nuevoEstado);
                throw new IllegalStateException("Estado no válido");
        }
        
        log.info("Validación de cambio de estado exitosa: {} -> {}", estadoActual, nuevoEstado);
    }

    public List<Requisicion> findAll() {
        log.info("Buscando todas las requisiciones");
        return requisicionRepository.findAll();
    }

    public Requisicion findById(Long id) {
        log.info("Buscando requisición con ID: {}", id);
        return requisicionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Requisición no encontrada con ID: {}", id);
                    return new RuntimeException("Requisición no encontrada con ID: " + id);
                });
    }

    public List<Requisicion> findByEstado(EstadoVacante estado) {
        log.info("Buscando requisiciones en estado: {}", estado);
        return requisicionRepository.findByEstado(estado);
    }
}