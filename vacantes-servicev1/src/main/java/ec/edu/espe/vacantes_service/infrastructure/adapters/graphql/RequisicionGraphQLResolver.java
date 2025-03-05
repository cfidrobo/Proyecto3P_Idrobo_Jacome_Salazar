package ec.edu.espe.vacantes_service.infrastructure.adapters.graphql;

import ec.edu.espe.vacantes_service.domain.entities.Requisicion;
import ec.edu.espe.vacantes_service.domain.entities.EstadoVacante;
import ec.edu.espe.vacantes_service.domain.entities.TipoReclutamiento;
import ec.edu.espe.vacantes_service.application.services.RequisicionService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
public class RequisicionGraphQLResolver {
    private final RequisicionService requisicionService;

    public RequisicionGraphQLResolver(RequisicionService requisicionService) {
        this.requisicionService = requisicionService;
    }

    @QueryMapping
    public List<Requisicion> getRequisiciones() {
        try {
            log.info("Obteniendo todas las requisiciones");
            return requisicionService.findAll();
        } catch (Exception e) {
            log.error("Error al obtener requisiciones", e);
            throw new RuntimeException("Error al obtener requisiciones: " + e.getMessage());
        }
    }

    @QueryMapping
    public Requisicion getRequisicionById(@Argument Long id) {
        try {
            log.info("Buscando requisición con ID: {}", id);
            return requisicionService.findById(id);
        } catch (Exception e) {
            log.error("Error al buscar requisición con ID: {}", id, e);
            throw new RuntimeException("Error al buscar requisición: " + e.getMessage());
        }
    }

    @QueryMapping
    public List<Requisicion> getRequisicionesPorEstado(@Argument EstadoVacante estado) {
        try {
            log.info("Buscando requisiciones en estado: {}", estado);
            return requisicionService.findByEstado(estado);
        } catch (Exception e) {
            log.error("Error al buscar requisiciones por estado: {}", e.getMessage());
            throw new RuntimeException("Error al buscar requisiciones por estado: " + e.getMessage());
        }
    }

    @MutationMapping
    public Requisicion createRequisicion(
            @Argument String cargo,
            @Argument String descripcion,
            @Argument String categoriaSalarial,
            @Argument String perfil,
            @Argument String fechaLimiteConvocatoria,
            @Argument TipoReclutamiento tipoReclutamiento) {
        try {
            log.info("Creando nueva requisición para cargo: {}", cargo);
            
            Requisicion requisicion = new Requisicion();
            requisicion.setCargo(cargo);
            requisicion.setDescripcion(descripcion);
            requisicion.setCategoriaSalarial(categoriaSalarial);
            requisicion.setPerfil(perfil);
            requisicion.setFechaLimiteConvocatoria(LocalDateTime.parse(fechaLimiteConvocatoria));
            requisicion.setTipoReclutamiento(tipoReclutamiento);
            
            return requisicionService.createRequisicion(requisicion);
        } catch (Exception e) {
            log.error("Error al crear requisición", e);
            throw new RuntimeException("Error al crear requisición: " + e.getMessage());
        }
    }

    @MutationMapping
    public Requisicion aprobarRequisicion(@Argument Long id) {
        try {
            log.info("Aprobando requisición con ID: {}", id);
            return requisicionService.aprobarRequisicion(id);
        } catch (Exception e) {
            log.error("Error al aprobar requisición con ID: {}", id, e);
            throw new RuntimeException("Error al aprobar requisición: " + e.getMessage());
        }
    }

    @MutationMapping
    public Requisicion enviarParaAprobacion(@Argument Long id) {
        try {
            log.info("Enviando para aprobación requisición con ID: {}", id);
            return requisicionService.enviarParaAprobacion(id);
        } catch (Exception e) {
            log.error("Error al enviar para aprobación requisición con ID: {}", id, e);
            throw new RuntimeException("Error al enviar para aprobación: " + e.getMessage());
        }
    }

    @MutationMapping
    public Requisicion cerrarVacante(@Argument Long id) {
        try {
            log.info("Cerrando vacante con ID: {}", id);
            return requisicionService.cerrarVacante(id);
        } catch (Exception e) {
            log.error("Error al cerrar vacante con ID: {}", id, e);
            throw new RuntimeException("Error al cerrar vacante: " + e.getMessage());
        }
    }

    @MutationMapping
    public boolean deleteRequisicion(@Argument Long id) {
        try {
            return requisicionService.deleteRequisicion(id);
        } catch (Exception e) {
            log.error("Error al eliminar requisición con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar requisición: " + e.getMessage());
        }
    }

    @MutationMapping
    public Requisicion updateRequisicion(
            @Argument Long id,
            @Argument String cargo,
            @Argument String descripcion,
            @Argument String categoriaSalarial,
            @Argument String perfil,
            @Argument String fechaLimiteConvocatoria,
            @Argument TipoReclutamiento tipoReclutamiento) {
        try {
            log.info("Actualizando requisición ID: {}", id);
            
            Requisicion requisicionActualizada = new Requisicion();
            requisicionActualizada.setCargo(cargo);
            requisicionActualizada.setDescripcion(descripcion);
            requisicionActualizada.setCategoriaSalarial(categoriaSalarial);
            requisicionActualizada.setPerfil(perfil);
            if (fechaLimiteConvocatoria != null) {
                requisicionActualizada.setFechaLimiteConvocatoria(
                    LocalDateTime.parse(fechaLimiteConvocatoria));
            }
            requisicionActualizada.setTipoReclutamiento(tipoReclutamiento);
            
            return requisicionService.updateRequisicion(id, requisicionActualizada);
        } catch (Exception e) {
            log.error("Error al actualizar requisición: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar requisición: " + e.getMessage());
        }
    }
}