package ec.edu.espe.entrevistas_service;

import ec.edu.espe.entrevistas_service.application.services.ReporteEntrevistaService;
import ec.edu.espe.entrevistas_service.domain.entities.Entrevista;
import ec.edu.espe.entrevistas_service.domain.entities.TipoEntrevista;
import ec.edu.espe.entrevistas_service.domain.repositories.EntrevistaRepository;
import ec.edu.espe.entrevistas_service.infrastructure.dtos.ReporteEntrevista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReporteEntrevistaServiceTest {

    @Mock
    private EntrevistaRepository entrevistaRepository;

    private ReporteEntrevistaService reporteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reporteService = new ReporteEntrevistaService(entrevistaRepository);
    }

    @Test
    void whenGenerarReportePorTipo_thenReturnReporte() {
        // Arrange
        TipoEntrevista tipo = TipoEntrevista.TECNICA;
        List<Entrevista> entrevistas = Arrays.asList(
            new Entrevista(), new Entrevista(), new Entrevista()
        );
        
        when(entrevistaRepository.findByTipo(tipo)).thenReturn(entrevistas);
        when(entrevistaRepository.countApprovedByTipo(tipo)).thenReturn(2L);
        when(entrevistaRepository.findPromedioCalificacionByTipo(tipo)).thenReturn(8.5);

        // Act
        ReporteEntrevista reporte = reporteService.generarReportePorTipo(tipo);

        // Assert
        assertNotNull(reporte);
        assertEquals(tipo, reporte.getTipo());
        assertEquals(3, reporte.getTotalEntrevistas());
        assertEquals(2, reporte.getEntrevistasAprobadas());
        assertEquals(8.5, reporte.getPromedioCalificacion());
        verify(entrevistaRepository).findByTipo(tipo);
    }
}