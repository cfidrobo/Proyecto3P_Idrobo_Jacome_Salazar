package ec.edu.espe.entrevistas_service;

import ec.edu.espe.entrevistas_service.domain.entities.Seleccion;
import ec.edu.espe.entrevistas_service.application.services.SeleccionService;
import ec.edu.espe.entrevistas_service.application.usecases.SeleccionarCandidatoUseCase;
import ec.edu.espe.entrevistas_service.domain.repositories.SeleccionRepository;
import ec.edu.espe.entrevistas_service.infrastructure.adapters.messaging.KafkaProducerAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeleccionServiceTest {

    @Mock
    private SeleccionarCandidatoUseCase seleccionarCandidatoUseCase;

    @Mock
    private SeleccionRepository seleccionRepository;

    @Mock
    private KafkaProducerAdapter kafkaProducer;

    private SeleccionService seleccionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seleccionService = new SeleccionService(
            seleccionarCandidatoUseCase, 
            seleccionRepository, 
            kafkaProducer
        );
    }

    @Test
    void whenSeleccionarCandidato_thenSaveAndNotify() {
        // Arrange
        Long candidatoId = 1L;
        String resultado = "SELECCIONADO";
        Seleccion seleccion = new Seleccion(candidatoId, LocalDateTime.now(), resultado);
        
        when(seleccionarCandidatoUseCase.execute(candidatoId, resultado))
            .thenReturn(seleccion);

        // Act
        Seleccion savedSeleccion = seleccionService.seleccionarCandidato(candidatoId, resultado);

        // Assert
        assertNotNull(savedSeleccion);
        assertEquals(candidatoId, savedSeleccion.getCandidatoId());
        assertEquals(resultado, savedSeleccion.getResultado());
        verify(kafkaProducer).sendSeleccionEvent(seleccion);
    }

    @Test
    void whenFindByCandidatoId_thenReturnSelecciones() {
        // Arrange
        Long candidatoId = 1L;
        List<Seleccion> expectedSelecciones = Arrays.asList(
            new Seleccion(candidatoId, LocalDateTime.now(), "SELECCIONADO"),
            new Seleccion(candidatoId, LocalDateTime.now(), "EN_PROCESO")
        );
        
        when(seleccionRepository.findByCandidatoId(candidatoId))
            .thenReturn(expectedSelecciones);

        // Act
        List<Seleccion> actualSelecciones = seleccionService.findByCandidatoId(candidatoId);

        // Assert
        assertEquals(expectedSelecciones.size(), actualSelecciones.size());
        verify(seleccionRepository).findByCandidatoId(candidatoId);
    }
}