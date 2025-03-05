package ec.edu.espe.entrevistas_service.application.usecases;

import org.springframework.stereotype.Service;
import ec.edu.espe.entrevistas_service.domain.entities.Entrevista;
import ec.edu.espe.entrevistas_service.domain.entities.Candidato;
import ec.edu.espe.entrevistas_service.infrastructure.dtos.EntrevistaInput;
import ec.edu.espe.entrevistas_service.domain.repositories.EntrevistaRepository;
import ec.edu.espe.entrevistas_service.application.services.CandidatoService;
import java.time.LocalDateTime;

@Service
public class CreateEntrevistaUseCase {
    private final EntrevistaRepository entrevistaRepository;
    private final CandidatoService candidatoService;

    public CreateEntrevistaUseCase(
            EntrevistaRepository entrevistaRepository,
            CandidatoService candidatoService) {
        this.entrevistaRepository = entrevistaRepository;
        this.candidatoService = candidatoService;
    }

    public Entrevista execute(EntrevistaInput input) {
        // Validar que el candidato tenga una postulación activa
        Candidato candidato = candidatoService.getCandidatoById(input.getCandidatoId());
        if (candidato == null || candidato.getVacanteId() == null) {
            throw new RuntimeException("El candidato debe tener una postulación activa para agendar una entrevista");
        }

        Entrevista entrevista = new Entrevista();
        entrevista.setCandidatoId(input.getCandidatoId());
        entrevista.setVacanteId(candidato.getVacanteId());
        entrevista.setFechaEntrevista(LocalDateTime.parse(input.getFechaEntrevista()));
        entrevista.setEntrevistador(input.getEntrevistador());
        entrevista.setTipo(input.getTipo());
        entrevista.setEstado("PROGRAMADA");
        
        return entrevistaRepository.save(entrevista);
    }
}