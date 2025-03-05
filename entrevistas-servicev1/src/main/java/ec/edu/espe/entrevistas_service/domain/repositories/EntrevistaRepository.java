package ec.edu.espe.entrevistas_service.domain.repositories;

import ec.edu.espe.entrevistas_service.domain.entities.Entrevista;
import ec.edu.espe.entrevistas_service.domain.entities.TipoEntrevista;
import ec.edu.espe.entrevistas_service.domain.entities.EstadoDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntrevistaRepository extends JpaRepository<Entrevista, Long> {
    Optional<Entrevista> findByCandidatoIdAndTipoAndEstado(Long candidatoId, TipoEntrevista tipo, String estado);
    List<Entrevista> findByCandidatoId(Long candidatoId);
    List<Entrevista> findByTipo(TipoEntrevista tipo);
    List<Entrevista> findByEstadoDecision(EstadoDecision estado);
    
    @Query("SELECT AVG(e.calificacion) FROM Entrevista e WHERE e.tipo = :tipo AND e.calificacion IS NOT NULL")
    Double findPromedioCalificacionByTipo(TipoEntrevista tipo);
    
    @Query("SELECT COUNT(e) FROM Entrevista e WHERE e.tipo = :tipo AND e.aprobada = true")
    Long countApprovedByTipo(TipoEntrevista tipo);
}