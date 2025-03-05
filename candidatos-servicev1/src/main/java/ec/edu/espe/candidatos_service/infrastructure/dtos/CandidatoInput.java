package ec.edu.espe.candidatos_service.infrastructure.dtos;

import ec.edu.espe.candidatos_service.domain.entities.TipoCandidato;
import lombok.Data;

@Data
public class CandidatoInput {
    private String nombre;
    private String correo;
    private String telefono;
    private String experiencia;
    private String habilidades;
    private TipoCandidato tipo;
}