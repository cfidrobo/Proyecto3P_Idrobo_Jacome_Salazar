package ec.edu.espe.vacantes_service.application.usecases;

import ec.edu.espe.vacantes_service.domain.entities.Requisicion;
import ec.edu.espe.vacantes_service.domain.repositories.RequisicionRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateRequisicionUseCase {

    private final RequisicionRepository requisicionRepository;

    public CreateRequisicionUseCase(RequisicionRepository requisicionRepository) {
        this.requisicionRepository = requisicionRepository;
    }

    public Requisicion execute(Requisicion requisicion) {
        // Se establece la fecha de creación
        requisicion.setFechaCreacion(java.time.LocalDateTime.now());
        return requisicionRepository.save(requisicion);
    }
}