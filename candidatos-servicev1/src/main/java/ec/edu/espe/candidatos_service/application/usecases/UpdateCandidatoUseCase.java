package ec.edu.espe.candidatos_service.application.usecases;

import ec.edu.espe.candidatos_service.domain.entities.Candidato;
import ec.edu.espe.candidatos_service.domain.repositories.CandidatoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateCandidatoUseCase {

    private final CandidatoRepository candidatoRepository;

    public UpdateCandidatoUseCase(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    public Candidato execute(Long id, Candidato candidatoActualizado) {
        Optional<Candidato> candidatoExistente = candidatoRepository.findById(id);
        
        if (candidatoExistente.isPresent()) {
            Candidato candidato = candidatoExistente.get();
            candidato.setNombre(candidatoActualizado.getNombre());
            candidato.setCorreo(candidatoActualizado.getCorreo());
            candidato.setTelefono(candidatoActualizado.getTelefono());
            candidato.setHabilidades(candidatoActualizado.getHabilidades());
            candidato.setExperiencia(candidatoActualizado.getExperiencia());
            
            return candidatoRepository.save(candidato);
        } else {
            throw new RuntimeException("Candidato no encontrado con ID: " + id);
        }
    }
}