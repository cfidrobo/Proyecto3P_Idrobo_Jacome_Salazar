package ec.edu.espe.entrevistas_service.infrastructure.dtos;

import lombok.Data;
import ec.edu.espe.entrevistas_service.domain.entities.TipoEntrevista;

@Data
public class EntrevistaInput {
    private Long candidatoId;
    private Long vacanteId;
    private String fechaEntrevista;
    private String entrevistador;
    private TipoEntrevista tipo;
    private String comentarios;
}