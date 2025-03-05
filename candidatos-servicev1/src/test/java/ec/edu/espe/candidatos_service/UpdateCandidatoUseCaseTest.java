package ec.edu.espe.candidatos_service;

import ec.edu.espe.candidatos_service.domain.entities.Candidato;
import ec.edu.espe.candidatos_service.domain.repositories.CandidatoRepository;
import ec.edu.espe.candidatos_service.application.usecases.UpdateCandidatoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateCandidatoUseCaseTest {

    @Mock
    private CandidatoRepository candidatoRepository;

    private UpdateCandidatoUseCase updateCandidatoUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        updateCandidatoUseCase = new UpdateCandidatoUseCase(candidatoRepository);
    }

    @Test
    void whenUpdateCandidato_thenSuccess() {
        // Arrange
        Long id = 1L;
        Candidato existingCandidato = new Candidato();
        existingCandidato.setId(id);
        existingCandidato.setNombre("Nombre Original");
        existingCandidato.setCorreo("original@mail.com");
        
        Candidato updatedData = new Candidato();
        updatedData.setId(id);
        updatedData.setNombre("Nombre Actualizado");
        updatedData.setCorreo("actualizado@mail.com");
        
        when(candidatoRepository.findById(id)).thenReturn(Optional.of(existingCandidato));
        when(candidatoRepository.save(any(Candidato.class))).thenReturn(updatedData);

        // Act
        Candidato result = updateCandidatoUseCase.execute(id, updatedData);

        // Assert
        assertNotNull(result);
        assertEquals("Nombre Actualizado", result.getNombre());
        assertEquals("actualizado@mail.com", result.getCorreo());
        verify(candidatoRepository).save(any(Candidato.class));
    }

    @Test
    void whenUpdateNonExistingCandidato_thenThrowException() {
        // Arrange
        Long id = 999L;
        Candidato updatedData = new Candidato();
        updatedData.setId(id);
        
        when(candidatoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            updateCandidatoUseCase.execute(id, updatedData);
        });
        
        verify(candidatoRepository, never()).save(any(Candidato.class));
    }
}