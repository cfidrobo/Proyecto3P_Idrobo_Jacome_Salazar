package ec.edu.espe.vacantes_service.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requisiciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Requisicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String cargo;
    
    @Column(nullable = false, length = 1000)
    private String descripcion;
    
    @Column(name = "categoria_salarial", nullable = false)
    private String categoriaSalarial;
    
    @Column(nullable = false, length = 1000)
    private String perfil;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVacante estado = EstadoVacante.BORRADOR;
    
    @Column(name = "fecha_limite_convocatoria")
    private LocalDateTime fechaLimiteConvocatoria;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_reclutamiento")
    private TipoReclutamiento tipoReclutamiento;
    
    @Column(name = "aprobado_por_rrhh")
    private boolean aprobadoPorRRHH;

    @ElementCollection
    @CollectionTable(name = "requisicion_finalistas", 
                    joinColumns = @JoinColumn(name = "requisicion_id"))
    private List<Long> finalistas = new ArrayList<>();

    public boolean isInformacionCompleta() {
        return cargo != null && !cargo.trim().isEmpty() &&
               descripcion != null && !descripcion.trim().isEmpty() &&
               categoriaSalarial != null && !categoriaSalarial.trim().isEmpty() &&
               perfil != null && !perfil.trim().isEmpty() &&
               fechaLimiteConvocatoria != null &&
               tipoReclutamiento != null;
    }

    public void agregarFinalista(Long candidatoId) {
        if (finalistas == null) {
            finalistas = new ArrayList<>();
        }
        finalistas.add(candidatoId);
    }
}