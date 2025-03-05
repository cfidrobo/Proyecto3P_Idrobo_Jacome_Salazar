package ec.edu.espe.entrevistas_service;

import ec.edu.espe.entrevistas_service.application.services.EntrevistaService;
import ec.edu.espe.entrevistas_service.domain.entities.Entrevista;
import ec.edu.espe.entrevistas_service.domain.entities.TipoEntrevista;
import ec.edu.espe.entrevistas_service.domain.entities.EstadoDecision;
import ec.edu.espe.entrevistas_service.domain.repositories.EntrevistaRepository;
import ec.edu.espe.entrevistas_service.infrastructure.adapters.messaging.KafkaProducerAdapter;
import ec.edu.espe.entrevistas_service.application.services.CandidatoService;
import ec.edu.espe.entrevistas_service.application.services.PostulacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EntrevistaServiceTest {

    @Mock
    private EntrevistaRepository entrevistaRepository;

    @Mock
    private KafkaProducerAdapter kafkaProducer;

    @Mock
    private CandidatoService candidatoService;

    @Mock
    private PostulacionService postulacionService;

    private EntrevistaService entrevistaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entrevistaService = new EntrevistaService(entrevistaRepository, candidatoService, kafkaProducer);
    }

    @Test
    void whenFindAll_thenReturnEntrevistaList() {
        // Arrange
        Entrevista entrevista1 = new Entrevista();
        entrevista1.setId(1L);
        Entrevista entrevista2 = new Entrevista();
        entrevista2.setId(2L);
        List<Entrevista> expectedEntrevistas = Arrays.asList(entrevista1, entrevista2);
        
        when(entrevistaRepository.findAll()).thenReturn(expectedEntrevistas);

        // Act
        List<Entrevista> actualEntrevistas = entrevistaService.findAll();

        // Assert
        assertEquals(expectedEntrevistas.size(), actualEntrevistas.size());
        verify(entrevistaRepository).findAll();
    }

    @Test
    void whenAgendarEntrevista_thenSaveAndReturnEntrevista() {
        // Arrange
        Entrevista entrevista = new Entrevista();
        entrevista.setCandidatoId(1L);
        entrevista.setVacanteId(1L);
        entrevista.setTipo(TipoEntrevista.PSICOLOGICA);
        entrevista.setFechaEntrevista(LocalDateTime.now().plusDays(1));
        
        when(postulacionService.tienePostulacionActiva(1L, 1L)).thenReturn(true);
        
        when(entrevistaRepository.save(any(Entrevista.class))).thenReturn(entrevista);
        when(entrevistaRepository.findByCandidatoIdAndTipoAndEstado(anyLong(), any(), eq("PROGRAMADA")))
            .thenReturn(Optional.empty());

        // Act
        Entrevista savedEntrevista = entrevistaService.agendarEntrevista(entrevista);

        // Assert
        assertNotNull(savedEntrevista);
        assertEquals(entrevista.getCandidatoId(), savedEntrevista.getCandidatoId());
        verify(postulacionService).tienePostulacionActiva(1L, 1L);
        verify(entrevistaRepository).save(any(Entrevista.class));
    }

    @Test
    void whenRegistrarCalificacion_thenUpdateAndNotify() {
        // Arrange
        Long entrevistaId = 1L;
        Entrevista entrevista = new Entrevista();
        entrevista.setId(entrevistaId);
        
        when(entrevistaRepository.findById(entrevistaId)).thenReturn(Optional.of(entrevista));
        when(entrevistaRepository.save(any(Entrevista.class))).thenReturn(entrevista);

        // Act
        Entrevista updatedEntrevista = entrevistaService.registrarCalificacion(
            entrevistaId, 8.5, true, "Excelente candidato"
        );

        // Assert
        assertNotNull(updatedEntrevista);
        assertEquals(8.5, updatedEntrevista.getCalificacion());
        assertTrue(updatedEntrevista.getAprobada());
        verify(kafkaProducer).sendEntrevistaCompletadaEvent(any(Entrevista.class));
    }

    @Test
    void whenActualizarEstadoDecision_thenUpdateAndNotify() {
        // Arrange
        Long entrevistaId = 1L;
        Entrevista entrevista = new Entrevista();
        entrevista.setId(entrevistaId);
        
        when(entrevistaRepository.findById(entrevistaId)).thenReturn(Optional.of(entrevista));
        when(entrevistaRepository.save(any(Entrevista.class))).thenReturn(entrevista);

        // Act
        Entrevista updatedEntrevista = entrevistaService.actualizarEstadoDecision(
            entrevistaId, 
            EstadoDecision.SELECCIONADO, 
            "Candidato seleccionado",
            "RRHH"
        );

        // Assert
        assertNotNull(updatedEntrevista);
        assertEquals(EstadoDecision.SELECCIONADO, updatedEntrevista.getEstadoDecision());
        verify(kafkaProducer).sendDecisionFinalEvent(any(Entrevista.class));
    }
}