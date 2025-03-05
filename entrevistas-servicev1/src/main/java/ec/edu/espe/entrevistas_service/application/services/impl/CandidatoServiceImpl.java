package ec.edu.espe.entrevistas_service.application.services.impl;

import ec.edu.espe.entrevistas_service.application.services.CandidatoService;
import ec.edu.espe.entrevistas_service.domain.entities.Candidato;
import org.springframework.stereotype.Service;

@Service
public class CandidatoServiceImpl implements CandidatoService {
    
    @Override
    public Candidato getCandidatoById(Long id) {
        // Por ahora retornamos un candidato mock
        // TODO: Implementar la lógica real para obtener el candidato
        Candidato candidato = new Candidato();
        candidato.setId(id);
        candidato.setVacanteId(1L); // valor mock
        return candidato;
    }
} 