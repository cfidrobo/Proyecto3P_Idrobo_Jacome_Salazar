package ec.edu.espe.candidatos_service.infrastructure.dtos;

import lombok.Data;

@Data
public class EvaluacionInput {
    private Long candidatoId;
    private Double puntaje;
    private String observaciones;
}