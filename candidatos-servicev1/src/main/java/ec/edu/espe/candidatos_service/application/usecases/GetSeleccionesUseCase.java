package ec.edu.espe.candidatos_service.application.usecases;

import ec.edu.espe.candidatos_service.domain.entities.Seleccion;
import ec.edu.espe.candidatos_service.domain.repositories.SeleccionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GetSeleccionesUseCase {
    private final SeleccionRepository seleccionRepository;

    public GetSeleccionesUseCase(SeleccionRepository seleccionRepository) {
        this.seleccionRepository = seleccionRepository;
    }

    public List<Seleccion> execute() {
        return seleccionRepository.findAll();
    }
} 