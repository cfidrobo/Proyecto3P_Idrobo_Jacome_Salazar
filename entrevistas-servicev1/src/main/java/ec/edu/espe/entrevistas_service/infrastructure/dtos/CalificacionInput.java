package ec.edu.espe.entrevistas_service.infrastructure.dtos;

import lombok.Data;

@Data
public class CalificacionInput {
    private Long entrevistaId;
    private Double calificacion;
    private Boolean aprobada;
    private String comentarios;
}