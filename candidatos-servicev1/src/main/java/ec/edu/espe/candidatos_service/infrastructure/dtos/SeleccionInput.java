package ec.edu.espe.candidatos_service.infrastructure.dtos;
import lombok.Data;

@Data
public class SeleccionInput {
    private Long candidatoId;
    private Long requisicionId;  // vacanteId
    private String fecha;
    private String estado;       // PENDIENTE, APROBADA, RECHAZADA
    private String comentarios;
}