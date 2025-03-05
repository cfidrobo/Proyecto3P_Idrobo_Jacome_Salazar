package ec.edu.espe.candidatos_service.domain.repositories;

import ec.edu.espe.candidatos_service.domain.entities.Seleccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeleccionRepository extends JpaRepository<Seleccion, Long> {
}