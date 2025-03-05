package ec.edu.espe.candidatos_service.application.usecases;

import ec.edu.espe.candidatos_service.domain.entities.Candidato;
import ec.edu.espe.candidatos_service.domain.repositories.CandidatoRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateCandidatoUseCase {

    private final CandidatoRepository candidatoRepository;

    public CreateCandidatoUseCase(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    public Candidato execute(Candidato candidato) {
        candidato.setFechaPostulacion(java.time.LocalDateTime.now());
        return candidatoRepository.save(candidato);
    }
}