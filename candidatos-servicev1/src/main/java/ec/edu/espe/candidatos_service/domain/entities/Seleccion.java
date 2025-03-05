package ec.edu.espe.candidatos_service.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Seleccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long candidatoId;
    private Long requisicionId;
    private LocalDateTime fecha;
    private String estado;
    private String comentarios;
}