package ec.edu.espe.entrevistas_service.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrevistas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entrevista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "candidato_id", nullable = false)
    private Long candidatoId;
    
    @Column(name = "vacante_id", nullable = false)
    private Long vacanteId;
    
    @Column(name = "fecha_entrevista", nullable = false)
    private LocalDateTime fechaEntrevista;
    
    @Column(nullable = false)
    private String entrevistador;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEntrevista tipo;
    
    @Column(nullable = false)
    private String estado;
    
    private Double calificacion;
    
    private Boolean aprobada;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_decision")
    private EstadoDecision estadoDecision;
    
    @Column(name = "fecha_decision")
    private LocalDateTime fechaDecision;
    
    @Column(name = "comentarios_decision")
    private String comentariosDecision;
    
    @Column(name = "area_aprobadora")
    private String areaAprobadora;
    
    private String comentarios;
    
    @PrePersist
    public void prePersist() {
        if (this.fechaEntrevista == null) {
            this.fechaEntrevista = LocalDateTime.now();
        }
        if (this.estadoDecision == null) {
            this.estadoDecision = EstadoDecision.PENDIENTE_DECISION;
        }
    }
}