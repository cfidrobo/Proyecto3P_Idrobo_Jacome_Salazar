package ec.edu.espe.entrevistas_service.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "selecciones")
public class Seleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long candidatoId;
    private LocalDateTime fechaSeleccion;
    private String resultado; // Ej: "SELECCIONADO", "NO SELECCIONADO"

    // Constructor vacío
    public Seleccion() { }

    public Seleccion(Long candidatoId, LocalDateTime fechaSeleccion, String resultado) {
        this.candidatoId = candidatoId;
        this.fechaSeleccion = fechaSeleccion;
        this.resultado = resultado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }
    public Long getCandidatoId() {
        return candidatoId;
    }
    public void setCandidatoId(Long candidatoId) {
        this.candidatoId = candidatoId;
    }
    public LocalDateTime getFechaSeleccion() {
        return fechaSeleccion;
    }
    public void setFechaSeleccion(LocalDateTime fechaSeleccion) {
        this.fechaSeleccion = fechaSeleccion;
    }
    public String getResultado() {
        return resultado;
    }
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}