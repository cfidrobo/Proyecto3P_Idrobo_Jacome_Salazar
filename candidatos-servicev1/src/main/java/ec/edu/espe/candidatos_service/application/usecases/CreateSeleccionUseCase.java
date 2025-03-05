package ec.edu.espe.candidatos_service.application.usecases;

import ec.edu.espe.candidatos_service.domain.entities.Seleccion;
import ec.edu.espe.candidatos_service.domain.repositories.SeleccionRepository;
import ec.edu.espe.candidatos_service.infrastructure.dtos.SeleccionInput;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CreateSeleccionUseCase {
    private final SeleccionRepository seleccionRepository;

    public CreateSeleccionUseCase(SeleccionRepository seleccionRepository) {
        this.seleccionRepository = seleccionRepository;
    }

    public Seleccion execute(SeleccionInput input) {
        Seleccion seleccion = new Seleccion();
        seleccion.setCandidatoId(input.getCandidatoId());
        seleccion.setRequisicionId(input.getRequisicionId());
        seleccion.setFecha(LocalDateTime.parse(input.getFecha()));
        seleccion.setEstado(input.getEstado());
        seleccion.setComentarios(input.getComentarios());
        
        return seleccionRepository.save(seleccion);
    }
}