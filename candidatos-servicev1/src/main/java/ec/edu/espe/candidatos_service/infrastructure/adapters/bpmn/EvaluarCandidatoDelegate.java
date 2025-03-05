package ec.edu.espe.candidatos_service.infrastructure.adapters.bpmn;

import ec.edu.espe.candidatos_service.application.services.CandidatoService;
import ec.edu.espe.candidatos_service.domain.entities.Candidato;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("evaluarCandidatoDelegate")
public class EvaluarCandidatoDelegate implements JavaDelegate {

    private final CandidatoService candidatoService;
    private static final Logger log = LoggerFactory.getLogger(EvaluarCandidatoDelegate.class);

    public EvaluarCandidatoDelegate(CandidatoService candidatoService) {
        this.candidatoService = candidatoService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long candidatoId = (Long) execution.getVariable("candidatoId");
        Double puntaje = (Double) execution.getVariable("puntajeEvaluacion");
        String observaciones = (String) execution.getVariable("observaciones");

        Candidato candidato = candidatoService.registrarEvaluacionPsicotecnica(candidatoId, puntaje, observaciones);
        
        log.info("Evaluación actualizada para candidato con ID: {}", candidato.getId());
        execution.setVariable("evaluacionCompletada", true);
    }
}