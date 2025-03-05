package ec.edu.espe.candidatos_service.infrastructure.adapters.graphql;

import ec.edu.espe.candidatos_service.application.services.ReporteService;
import ec.edu.espe.candidatos_service.infrastructure.dtos.HabilidadEstadistica;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Controller
@Slf4j
public class ReporteGraphQLController {
    private final ReporteService reporteService;

    public ReporteGraphQLController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @QueryMapping
    public List<HabilidadEstadistica> estadisticasHabilidades() {
        log.info("Ejecutando query estadisticasHabilidades");
        return reporteService.obtenerEstadisticasHabilidades();
    }
}