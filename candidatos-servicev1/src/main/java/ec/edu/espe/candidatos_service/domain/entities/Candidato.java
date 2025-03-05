package ec.edu.espe.candidatos_service.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidatos")
@Data
@NoArgsConstructor
public class Candidato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String correo;
    private String telefono;
    private String experiencia;
    private String habilidades;
    private Long vacanteId;
    
    @Enumerated(EnumType.STRING)
    private TipoCandidato tipo;
    
    @Enumerated(EnumType.STRING)
    private EstadoCandidato estado;
    
    private Double puntajePsicotecnico;
    private Double puntajeTecnico;
    private Double puntajeEntrevista;
    private Double puntajeTotal;
    
    private LocalDateTime fechaPostulacion;
    private LocalDateTime fechaUltimaEvaluacion;
    
    @Column(length = 1000)
    private String observaciones;
    
    public Candidato(String nombre, String correo, String telefono, 
                    String experiencia, String habilidades, TipoCandidato tipo) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.experiencia = experiencia;
        this.habilidades = habilidades;
        this.tipo = tipo;
        this.estado = EstadoCandidato.POSTULADO;
        this.fechaPostulacion = LocalDateTime.now();
    }
    
    public void calcularPuntajeTotal() {
        if (puntajePsicotecnico == null && puntajeTecnico == null && puntajeEntrevista == null) {
            this.puntajeTotal = null;
            return;
        }
        
        double psicotecnico = this.puntajePsicotecnico != null ? this.puntajePsicotecnico : 0;
        double tecnico = this.puntajeTecnico != null ? this.puntajeTecnico : 0;
        double entrevista = this.puntajeEntrevista != null ? this.puntajeEntrevista : 0;
        
        this.puntajeTotal = (psicotecnico * 0.3) + (tecnico * 0.4) + (entrevista * 0.3);
    }
    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = EstadoCandidato.POSTULADO;
        }
        if (this.fechaPostulacion == null) {
            this.fechaPostulacion = LocalDateTime.now();
        }
        calcularPuntajeTotal();
    }
}