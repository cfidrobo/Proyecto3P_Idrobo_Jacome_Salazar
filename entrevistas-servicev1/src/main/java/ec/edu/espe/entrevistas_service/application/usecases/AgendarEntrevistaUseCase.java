package ec.edu.espe.entrevistas_service.application.usecases;


import ec.edu.espe.entrevistas_service.domain.entities.Entrevista;
import ec.edu.espe.entrevistas_service.domain.repositories.EntrevistaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AgendarEntrevistaUseCase {

    private final EntrevistaRepository entrevistaRepository;

    public AgendarEntrevistaUseCase(EntrevistaRepository entrevistaRepository) {
        this.entrevistaRepository = entrevistaRepository;
    }

    public Entrevista execute(Long candidatoId, String entrevistador, String comentarios) {
        Entrevista entrevista = new Entrevista();
        entrevista.setCandidatoId(candidatoId);
        entrevista.setFechaEntrevista(LocalDateTime.now());
        entrevista.setEntrevistador(entrevistador);
        entrevista.setComentarios(comentarios);
        return entrevistaRepository.save(entrevista);
    }
}
