package ec.edu.espe.candidatos_service.infrastructure.adapters.graphql;

import ec.edu.espe.candidatos_service.domain.entities.Seleccion;
import ec.edu.espe.candidatos_service.infrastructure.dtos.SeleccionInput;
import ec.edu.espe.candidatos_service.application.usecases.CreateSeleccionUseCase;
import ec.edu.espe.candidatos_service.application.usecases.GetSeleccionesUseCase;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class SeleccionGraphQLController {
    private final CreateSeleccionUseCase createSeleccionUseCase;
    private final GetSeleccionesUseCase getSeleccionesUseCase;

    public SeleccionGraphQLController(CreateSeleccionUseCase createSeleccionUseCase,
                                    GetSeleccionesUseCase getSeleccionesUseCase) {
        this.createSeleccionUseCase = createSeleccionUseCase;
        this.getSeleccionesUseCase = getSeleccionesUseCase;
    }

    @MutationMapping
    public Seleccion createSeleccion(@Argument SeleccionInput input) {
        return createSeleccionUseCase.execute(input);
    }

    @QueryMapping
    public List<Seleccion> getSelecciones() {
        return getSeleccionesUseCase.execute();
    }
}