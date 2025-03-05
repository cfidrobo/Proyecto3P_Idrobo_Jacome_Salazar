package ec.edu.espe.entrevistas_service.infrastructure.dtos;

import ec.edu.espe.entrevistas_service.domain.entities.EstadoDecision;
import lombok.Data;

@Data
public class DecisionInput {
    private Long entrevistaId;
    private EstadoDecision estadoDecision;
    private String comentarios;
    private String areaAprobadora;
}
