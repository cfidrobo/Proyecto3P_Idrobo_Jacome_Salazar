package ec.edu.espe.candidatos_service.infrastructure.dtos;

import lombok.Data;

@Data
public class PostulacionInput {
    private Long candidatoId;
    private Long vacanteId;
} 