package ec.edu.espe.candidatos_service.infrastructure.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidatoMessage {
    private Long id;
    private String nombre;
    private String estado;
    private Double puntajeTotal;
    private String experiencia;
    private String habilidades;
    private Long vacanteId; 
    private String tipo; 
}