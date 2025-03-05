package ec.edu.espe.vacantes_service;

import ec.edu.espe.vacantes_service.domain.entities.Requisicion;
import ec.edu.espe.vacantes_service.domain.entities.EstadoVacante;
import ec.edu.espe.vacantes_service.domain.entities.TipoReclutamiento;
import ec.edu.espe.vacantes_service.domain.repositories.RequisicionRepository;
import ec.edu.espe.vacantes_service.infrastructure.adapters.messaging.KafkaProducerAdapter;
import ec.edu.espe.vacantes_service.application.services.RequisicionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequisicionServiceTest {

    @Mock
    private RequisicionRepository requisicionRepository;

    @Mock
    private KafkaProducerAdapter kafkaProducer;

    @InjectMocks
    private RequisicionService requisicionService;

    private Requisicion requisicion;

    @BeforeEach
    void setUp() {
        requisicion = new Requisicion();
        requisicion.setId(1L);
        requisicion.setCargo("Desarrollador Senior");
        requisicion.setDescripcion("Desarrollador Java con 5 años de experiencia");
        requisicion.setCategoriaSalarial("A");
        requisicion.setPerfil("Senior");
        requisicion.setEstado(EstadoVacante.BORRADOR);
        requisicion.setTipoReclutamiento(TipoReclutamiento.EXTERNO);
        requisicion.setFechaCreacion(LocalDateTime.now());
        requisicion.setFechaLimiteConvocatoria(LocalDateTime.now().plusDays(30));
    }

    @Test
    void enviarParaAprobacion_DebeActualizarEstadoYGuardar() {
        // Arrange
        when(requisicionRepository.findById(1L)).thenReturn(Optional.of(requisicion));
        when(requisicionRepository.save(any(Requisicion.class))).thenReturn(requisicion);

        // Act
        Requisicion resultado = requisicionService.enviarParaAprobacion(1L);

        // Assert
        assertEquals(EstadoVacante.PENDIENTE_APROBACION, resultado.getEstado());
        verify(requisicionRepository).save(any(Requisicion.class));
    }

    @Test
    void aprobarRequisicion_DebeActualizarEstadoYNotificar() {
        // Arrange
        requisicion.setEstado(EstadoVacante.PENDIENTE_APROBACION);
        when(requisicionRepository.findById(1L)).thenReturn(Optional.of(requisicion));
        when(requisicionRepository.save(any(Requisicion.class))).thenReturn(requisicion);
        doNothing().when(kafkaProducer).sendVacantePublishedEvent(any(Requisicion.class));

        // Act
        Requisicion resultado = requisicionService.aprobarRequisicion(1L);

        // Assert
        assertEquals(EstadoVacante.PUBLICADA, resultado.getEstado());
        verify(requisicionRepository).save(any(Requisicion.class));
        verify(kafkaProducer).sendVacantePublishedEvent(any(Requisicion.class));
    }
}