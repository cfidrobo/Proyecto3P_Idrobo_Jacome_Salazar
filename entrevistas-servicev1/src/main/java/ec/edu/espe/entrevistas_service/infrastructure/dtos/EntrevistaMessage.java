package ec.edu.espe.entrevistas_service.infrastructure.dtos;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntrevistaMessage {
    private Long id;
    private Long candidatoId;
    private LocalDateTime fechaEntrevista;
    private String entrevistador;
    private String comentarios;
    private String estado;
}