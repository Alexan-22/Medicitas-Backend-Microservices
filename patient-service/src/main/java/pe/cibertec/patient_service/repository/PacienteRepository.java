package pe.cibertec.patient_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.patient_service.entity.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
	long countByEstado(String estado);
	Optional<Paciente> findByUsuarioId(Long usuarioId);
}