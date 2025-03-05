package ec.edu.espe.candidatos_service.application.usecases;

import ec.edu.espe.candidatos_service.domain.repositories.CandidatoRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeleteCandidatoUseCase {
    private final CandidatoRepository candidatoRepository;

    public DeleteCandidatoUseCase(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    public boolean execute(Long id) {
        try {
            if (!candidatoRepository.existsById(id)) {
                return false;
            }
            candidatoRepository.deleteById(id);
            log.info("Candidato eliminado con ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("Error al eliminar candidato con ID {}: {}", id, e.getMessage());
            return false;
        }
    }
}