package pe.cibertec.patient_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.cibertec.patient_service.entity.Paciente;

public interface PacienteRepository
        extends JpaRepository<Paciente, Long> {
	
		long countByEstado(String estado);
}