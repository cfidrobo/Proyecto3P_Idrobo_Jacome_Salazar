package ec.edu.espe.candidatos_service.infrastructure.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabilidadEstadistica {
    private String habilidad;
    private Integer cantidad;
    private Double porcentajeCandidatos;
}