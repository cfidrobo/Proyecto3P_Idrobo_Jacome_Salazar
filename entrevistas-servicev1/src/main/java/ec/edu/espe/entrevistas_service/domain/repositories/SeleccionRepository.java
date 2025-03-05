package ec.edu.espe.entrevistas_service.domain.repositories;


import ec.edu.espe.entrevistas_service.domain.entities.Seleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeleccionRepository extends JpaRepository<Seleccion, Long> {
    List<Seleccion> findByCandidatoId(Long candidatoId);
}
