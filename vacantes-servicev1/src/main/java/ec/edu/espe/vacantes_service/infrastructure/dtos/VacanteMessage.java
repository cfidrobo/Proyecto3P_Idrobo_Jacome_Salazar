package ec.edu.espe.vacantes_service.infrastructure.dtos;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacanteMessage {
    private Long id;
    private String cargo;
    private String descripcion;
    private String categoriaSalarial;
    private String perfil;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLimiteConvocatoria;
    private String estado;
    private String tipoReclutamiento;
    private boolean aprobadoPorRRHH;
}