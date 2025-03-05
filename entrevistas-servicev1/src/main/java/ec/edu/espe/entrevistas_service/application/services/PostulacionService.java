package ec.edu.espe.entrevistas_service.application.services;

public interface PostulacionService {
    boolean tienePostulacionActiva(Long candidatoId, Long vacanteId);
} 