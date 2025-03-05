package ec.edu.espe.entrevistas_service.infrastructure.adapters.graphql;

import ec.edu.espe.entrevistas_service.application.services.EntrevistaService;
import ec.edu.espe.entrevistas_service.application.services.ReporteEntrevistaService;
import ec.edu.espe.entrevistas_service.domain.entities.Entrevista;
import ec.edu.espe.entrevistas_service.domain.entities.TipoEntrevista;
import ec.edu.espe.entrevistas_service.infrastructure.dtos.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Controller
@Slf4j
public class EntrevistaGraphQLController {
    private final EntrevistaService entrevistaService;
    private final ReporteEntrevistaService reporteService;

    public EntrevistaGraphQLController(EntrevistaService entrevistaService,
                                     ReporteEntrevistaService reporteService) {
        this.entrevistaService = entrevistaService;
        this.reporteService = reporteService;
    }

    @QueryMapping
    public List<Entrevista> entrevistas() {
        return entrevistaService.findAll();
    }

    @QueryMapping
    public Entrevista entrevista(@Argument Long id) {
        return entrevistaService.findById(id);
    }

    @QueryMapping
    public List<Entrevista> entrevistasPorTipo(@Argument TipoEntrevista tipo) {
        return entrevistaService.findByTipo(tipo);
    }

    @QueryMapping
    public ReporteEntrevista reporteEntrevistas(@Argument TipoEntrevista tipo) {
        return reporteService.generarReportePorTipo(tipo);
    }

    @MutationMapping
    public Entrevista agendarEntrevista(@Argument EntrevistaInput input) {
        Entrevista entrevista = new Entrevista();
        entrevista.setCandidatoId(input.getCandidatoId());
        entrevista.setVacanteId(input.getVacanteId());
        entrevista.setEntrevistador(input.getEntrevistador());
        entrevista.setTipo(input.getTipo());
        entrevista.setComentarios(input.getComentarios());
        
        return entrevistaService.agendarEntrevista(entrevista);
    }

    @MutationMapping
    public Entrevista registrarCalificacion(@Argument CalificacionInput input) {
        return entrevistaService.registrarCalificacion(
            input.getEntrevistaId(),
            input.getCalificacion(),
            input.getAprobada(),
            input.getComentarios()
        );
    }

    @MutationMapping
    public Entrevista actualizarEstadoDecision(@Argument DecisionInput input) {
        return entrevistaService.actualizarEstadoDecision(
            input.getEntrevistaId(),
            input.getEstadoDecision(),
            input.getComentarios(),
            input.getAreaAprobadora()
        );
    }
}