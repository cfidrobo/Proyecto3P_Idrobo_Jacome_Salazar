package ec.edu.espe.candidatos_service.infrastructure.adapters.graphql;

import ec.edu.espe.candidatos_service.domain.entities.Candidato;
import ec.edu.espe.candidatos_service.domain.entities.EstadoCandidato;
import ec.edu.espe.candidatos_service.domain.entities.TipoCandidato;
import ec.edu.espe.candidatos_service.application.services.CandidatoService;
import ec.edu.espe.candidatos_service.infrastructure.dtos.CandidatoInput;
import ec.edu.espe.candidatos_service.infrastructure.dtos.EvaluacionInput;
import ec.edu.espe.candidatos_service.infrastructure.dtos.PostulacionInput;
import ec.edu.espe.candidatos_service.application.usecases.UpdateCandidatoUseCase;
import ec.edu.espe.candidatos_service.application.usecases.DeleteCandidatoUseCase;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Controller
@Slf4j
public class CandidatoGraphQLResolver {

    private final CandidatoService candidatoService;
    private final UpdateCandidatoUseCase updateCandidatoUseCase;
    private final DeleteCandidatoUseCase deleteCandidatoUseCase;



    public CandidatoGraphQLResolver(CandidatoService candidatoService, UpdateCandidatoUseCase updateCandidatoUseCase, DeleteCandidatoUseCase deleteCandidatoUseCase) {
        this.candidatoService = candidatoService;
        this.updateCandidatoUseCase = updateCandidatoUseCase;
        this.deleteCandidatoUseCase = deleteCandidatoUseCase;
    }

    @QueryMapping
    public List<Candidato> candidatos() {
        log.info("Obteniendo todos los candidatos");
        return candidatoService.findAll();
    }

    @QueryMapping
    public Candidato candidato(@Argument Long id) {
        log.info("Buscando candidato con ID: {}", id);
        return candidatoService.findById(id);
    }

    @QueryMapping
    public List<Candidato> candidatosPorEstado(@Argument EstadoCandidato estado) {
        log.info("Buscando candidatos en estado: {}", estado);
        return candidatoService.findByEstado(estado);
    }

    @QueryMapping
    public List<Candidato> candidatosPorTipo(@Argument TipoCandidato tipo) {
        log.info("Buscando candidatos de tipo: {}", tipo);
        return candidatoService.findByTipo(tipo);
    }

    @MutationMapping
    public Candidato createCandidato(@Argument("candidato") CandidatoInput input) {
        log.info("Creando nuevo candidato: {}", input.getNombre());
        Candidato candidato = new Candidato(
            input.getNombre(),
            input.getCorreo(),
            input.getTelefono(),
            input.getExperiencia(),
            input.getHabilidades(),
            input.getTipo()
        );
        return candidatoService.createCandidato(candidato);
    }

    @MutationMapping
    public Candidato registrarEvaluacionPsicotecnica(@Argument("evaluacion") EvaluacionInput input) {
        log.info("Registrando evaluación psicotécnica para candidato ID: {}", input.getCandidatoId());
        return candidatoService.registrarEvaluacionPsicotecnica(
            input.getCandidatoId(),
            input.getPuntaje(),
            input.getObservaciones()
        );
    }

    @MutationMapping
    public Candidato registrarEvaluacionTecnica(@Argument("evaluacion") EvaluacionInput input) {
        log.info("Registrando evaluación técnica para candidato ID: {}", input.getCandidatoId());
        return candidatoService.registrarEvaluacionTecnica(
            input.getCandidatoId(),
            input.getPuntaje(),
            input.getObservaciones()
        );
    }

    @MutationMapping
    public Candidato registrarEntrevista(@Argument("evaluacion") EvaluacionInput input) {
        log.info("Registrando entrevista para candidato ID: {}", input.getCandidatoId());
        return candidatoService.registrarEntrevista(
            input.getCandidatoId(),
            input.getPuntaje(),
            input.getObservaciones()
        );
    }

    @MutationMapping
    public Candidato seleccionarCandidato(@Argument Long id) {
        log.info("Seleccionando candidato ID: {}", id);
        return candidatoService.seleccionarCandidato(id);
    }

    @MutationMapping
    public Candidato rechazarCandidato(@Argument Long id, @Argument String motivo) {
        log.info("Rechazando candidato ID: {} - Motivo: {}", id, motivo);
        return candidatoService.rechazarCandidato(id, motivo);
    }
    @MutationMapping
    public Candidato postularAVacante(@Argument("postulacion") PostulacionInput input) {
        log.info("Postulando candidato ID: {} a vacante ID: {}", 
                input.getCandidatoId(), input.getVacanteId());
        return candidatoService.postularAVacante(
            input.getCandidatoId(),
            input.getVacanteId()
        );
    }
    @MutationMapping
    public Candidato actualizarCandidato(@Argument Long id, @Argument CandidatoInput input) {
        Candidato candidato = new Candidato();
        candidato.setNombre(input.getNombre());
        candidato.setCorreo(input.getCorreo());
        candidato.setTelefono(input.getTelefono());
        candidato.setExperiencia(input.getExperiencia());
        candidato.setHabilidades(input.getHabilidades());
        candidato.setTipo(input.getTipo());
        
        return updateCandidatoUseCase.execute(id, candidato);
    }
    @MutationMapping
    public boolean eliminarCandidato(@Argument Long id) {
        return deleteCandidatoUseCase.execute(id);
    }
}