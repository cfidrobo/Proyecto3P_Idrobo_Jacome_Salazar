package ec.edu.espe.entrevistas_service.application.services;

import ec.edu.espe.entrevistas_service.domain.entities.Entrevista;
import ec.edu.espe.entrevistas_service.domain.entities.TipoEntrevista;
import ec.edu.espe.entrevistas_service.domain.repositories.EntrevistaRepository;
import ec.edu.espe.entrevistas_service.infrastructure.dtos.ReporteEntrevista;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class ReporteEntrevistaService {
    private final EntrevistaRepository entrevistaRepository;

    public ReporteEntrevistaService(EntrevistaRepository entrevistaRepository) {
        this.entrevistaRepository = entrevistaRepository;
    }

    public ReporteEntrevista generarReportePorTipo(TipoEntrevista tipo) {
        List<Entrevista> entrevistas = entrevistaRepository.findByTipo(tipo);
        
        long total = entrevistas.size();
        long aprobadas = entrevistaRepository.countApprovedByTipo(tipo);
        double promedio = entrevistaRepository.findPromedioCalificacionByTipo(tipo) != null ? 
            entrevistaRepository.findPromedioCalificacionByTipo(tipo) : 0.0;
        
        Duration tiempoPromedio = calcularTiempoPromedioDecision(entrevistas);
        
        return new ReporteEntrevista(
            tipo,
            total,
            promedio,
            aprobadas,
            total - aprobadas,
            total > 0 ? (double) aprobadas / total * 100 : 0.0,
            tiempoPromedio
        );
    }

    private Duration calcularTiempoPromedioDecision(List<Entrevista> entrevistas) {
        return entrevistas.stream()
            .filter(e -> e.getFechaDecision() != null)
            .map(e -> Duration.between(e.getFechaEntrevista(), e.getFechaDecision()))
            .reduce(Duration.ZERO, Duration::plus)
            .dividedBy(entrevistas.size() > 0 ? entrevistas.size() : 1);
    }
}