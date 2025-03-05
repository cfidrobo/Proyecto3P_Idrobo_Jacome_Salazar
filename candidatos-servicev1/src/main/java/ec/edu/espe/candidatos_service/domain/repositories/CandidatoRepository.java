package ec.edu.espe.candidatos_service.domain.repositories;

import ec.edu.espe.candidatos_service.domain.entities.Candidato;
import ec.edu.espe.candidatos_service.domain.entities.EstadoCandidato;
import ec.edu.espe.candidatos_service.domain.entities.TipoCandidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatoRepository extends JpaRepository<Candidato, Long> {
    List<Candidato> findByEstado(EstadoCandidato estado);
    List<Candidato> findByTipo(TipoCandidato tipo);
} 