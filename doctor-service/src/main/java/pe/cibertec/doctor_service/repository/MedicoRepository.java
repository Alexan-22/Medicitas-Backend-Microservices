package pe.cibertec.doctor_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.doctor_service.entity.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
	long countByEstado(String estado);
	Optional<Medico> findByUsuarioId(Long usuarioId);
}
