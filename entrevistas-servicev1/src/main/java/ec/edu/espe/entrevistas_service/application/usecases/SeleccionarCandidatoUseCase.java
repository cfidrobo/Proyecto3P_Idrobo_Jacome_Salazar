package ec.edu.espe.entrevistas_service.application.usecases;


import ec.edu.espe.entrevistas_service.domain.entities.Seleccion;
import ec.edu.espe.entrevistas_service.domain.repositories.SeleccionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class SeleccionarCandidatoUseCase {

    private final SeleccionRepository seleccionRepository;

    public SeleccionarCandidatoUseCase(SeleccionRepository seleccionRepository) {
        this.seleccionRepository = seleccionRepository;
    }

    public Seleccion execute(Long candidatoId, String resultado) {
        Seleccion seleccion = new Seleccion(candidatoId, LocalDateTime.now(), resultado);
        return seleccionRepository.save(seleccion);
    }
}
