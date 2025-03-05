package ec.edu.espe.candidatos_service.application.services;

import ec.edu.espe.candidatos_service.domain.entities.Candidato;
import ec.edu.espe.candidatos_service.domain.entities.EstadoCandidato;
import ec.edu.espe.candidatos_service.domain.repositories.CandidatoRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import ec.edu.espe.candidatos_service.infrastructure.dtos.HabilidadEstadistica;

@Service
@Slf4j
public class ReporteService {
    private final CandidatoRepository candidatoRepository;

    public ReporteService(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    public Map<EstadoCandidato, Long> obtenerEstadisticasPorEstado() {
        List<Candidato> candidatos = candidatoRepository.findAll();
        return candidatos.stream()
            .collect(Collectors.groupingBy(
                Candidato::getEstado,
                Collectors.counting()
            ));
    }

    public List<Candidato> obtenerCandidatosFinalistasRecientes(int dias) {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(dias);
        return candidatoRepository.findByEstado(EstadoCandidato.FINALISTA).stream()
            .filter(c -> c.getFechaUltimaEvaluacion().isAfter(fechaLimite))
            .collect(Collectors.toList());
    }

    public double calcularPromedioEvaluacionesTecnicas() {
        List<Candidato> candidatos = candidatoRepository.findAll();
        return candidatos.stream()
            .filter(c -> c.getPuntajeTecnico() != null)
            .mapToDouble(Candidato::getPuntajeTecnico)
            .average()
            .orElse(0.0);
    }

    public Map<String, Long> obtenerEstadisticasPorHabilidad() {
        return candidatoRepository.findAll().stream()
            .flatMap(c -> List.of(c.getHabilidades().split(",")).stream())
            .map(String::trim)
            .collect(Collectors.groupingBy(
                h -> h,
                Collectors.counting()
            ));
    }

    public List<HabilidadEstadistica> obtenerEstadisticasHabilidades() {
        log.info("Generando estadísticas de habilidades");
        List<Candidato> candidatos = candidatoRepository.findAll();
        
        // Siempre retornar una lista (vacía si no hay datos)
        if (candidatos.isEmpty()) {
            log.info("No hay candidatos registrados");
            return new ArrayList<>();
        }

        Map<String, Integer> habilidadesCount = new HashMap<>();
        int totalCandidatos = candidatos.size();

        // Contar habilidades
        for (Candidato candidato : candidatos) {
            if (candidato.getHabilidades() != null && !candidato.getHabilidades().trim().isEmpty()) {
                String[] habilidades = candidato.getHabilidades().split(",");
                for (String habilidad : habilidades) {
                    String habilidadTrim = habilidad.trim();
                    habilidadesCount.merge(habilidadTrim, 1, Integer::sum);
                }
            }
        }

        // Convertir a lista de estadísticas
        List<HabilidadEstadistica> estadisticas = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : habilidadesCount.entrySet()) {
            double porcentaje = (entry.getValue() * 100.0) / totalCandidatos;
            estadisticas.add(new HabilidadEstadistica(
                entry.getKey(),
                entry.getValue(),
                porcentaje
            ));
        }

        log.info("Estadísticas generadas: {} habilidades encontradas", estadisticas.size());
        return estadisticas;
    }
}