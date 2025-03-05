package ec.edu.espe.entrevistas_service.infrastructure.adapters.bpmn;

import ec.edu.espe.entrevistas_service.application.services.SeleccionService;
import ec.edu.espe.entrevistas_service.domain.entities.Seleccion;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("seleccionDelegate")
public class SeleccionDelegate implements JavaDelegate {

    private final SeleccionService seleccionService;

    public SeleccionDelegate(SeleccionService seleccionService) {
        this.seleccionService = seleccionService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Se asume que el BPMN envía el ID del candidato y el resultado de selección
        Long candidatoId = (Long) execution.getVariable("candidatoId");
        String resultado = (String) execution.getVariable("resultado");

        Seleccion seleccion = seleccionService.seleccionarCandidato(candidatoId, resultado);
        System.out.println("Proceso de selección completado para candidato ID: " + seleccion.getCandidatoId());
        // Opcional: emitir un evento vía Kafka notificando la selección final
    }
}
