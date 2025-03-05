package ec.edu.espe.vacantes_service.domain.repositories;

import ec.edu.espe.vacantes_service.domain.entities.Requisicion;
import ec.edu.espe.vacantes_service.domain.entities.EstadoVacante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequisicionRepository extends JpaRepository<Requisicion, Long> {
    List<Requisicion> findByEstado(EstadoVacante estado);
}