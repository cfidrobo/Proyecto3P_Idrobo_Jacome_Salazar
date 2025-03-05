package ec.edu.espe.entrevistas_service.infrastructure.dtos;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ec.edu.espe.entrevistas_service.domain.entities.TipoEntrevista;
import java.time.Duration;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteEntrevista {
    private TipoEntrevista tipo;
    private Long totalEntrevistas;
    private Double promedioCalificacion;
    private Long entrevistasAprobadas;
    private Long entrevistasRechazadas;
    private Double tasaAprobacion;
    private Duration tiempoPromedioDecision;
}